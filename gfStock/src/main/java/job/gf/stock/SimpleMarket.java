package job.gf.stock;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import job.gf.stock.generate.CrazyTradeInfoGenerator;
import job.gf.stock.generate.ITradeInfoGenerator;
import job.gf.stock.generate.RandomTradeInfoGenerator;
import job.gf.stock.generate.TenTradeInfoGenerator;
import job.gf.stock.msg.Notice;
import job.gf.stock.taskQueue.TaskAction;
import job.gf.stock.taskQueue.TaskQueue;
import job.gf.util.IoUtil;

/**
 * 单线程实现股票信息更新
 * 
 * 按下一次更新时间,由小到大排列,更新股票信息
 * @author ouotuo
 *
 */
public class SimpleMarket extends AbstractMarket {
	Logger log = LoggerFactory.getLogger(getClass());
	static final String STOCK_FILE="/stocks.cnf";
	
	static final int ACTION_NOTICE=10;
	static final int ACTION_STOCK=20;
	static final int ACTION_MARKET=30; 
	static final int ACTION_INDEX=100; 
	
	static final long REST_TIME=10000;//10s
	
	TaskQueue mTaskQueue = new TaskQueue();
	boolean mIsRunning = false;
	MarketThread mMarketThread=new MarketThread();
	
	Map<String,Stock> mStockMap = new HashMap<String,Stock>(16);
	
	MarketInfo mMarketInfo = new MarketInfo();
	
	@Override
	public void addStock(Stock stock) {
		mStockMap.put(stock.getCode(), stock);
		
		TaskAction ta = TaskAction.createAgainTaskAction("stock:"+stock.getCode(),stock.getUpdateTime());
		ta.setParams(stock.getCode());
		ta.setAction(ACTION_STOCK);
		mTaskQueue.addTask(ta);
		
		Notice tipNotice = Notice.createTipNotice("恭喜["+stock.getName()+"]加入股市", "哈");
		TaskAction tipNoticeTA = TaskAction.createTimesTaskAction("tip",1);
		tipNoticeTA.setParams(tipNotice);
		tipNoticeTA.setAction(ACTION_NOTICE);
		mTaskQueue.addTask(tipNoticeTA);
	}

	@Override
	public boolean removeStock(String code) {
		return mStockMap.remove(code)!=null;
	}

	@Override
	public boolean stopStock(String code) {
		
		return false;
	}
	
	private void addNoticeTask(){
		//tip
		Notice tipNotice = Notice.createTipNotice("股市有风险,投资请谨慎!", "haha");
		TaskAction tipNoticeTA = TaskAction.createAgainTaskAction("tip",TaskAction.MINUTE_MS*5);
		tipNoticeTA.setParams(tipNotice);
		tipNoticeTA.setAction(ACTION_NOTICE);
		mTaskQueue.addTask(tipNoticeTA);
	}
	
	private void addCaculateIndexTask(){
		//tip
		TaskAction ta = TaskAction.createAgainTaskAction("index",TaskAction.SECOND_MS);
		ta.setAction(ACTION_INDEX);
		mTaskQueue.addTask(ta);
	}
	
	private void addMarketTask(){
		//tip
		TaskAction ta = TaskAction.createAgainTaskAction("market",TaskAction.SECOND_MS);
		ta.setAction(ACTION_MARKET);
		mTaskQueue.addTask(ta);
	}
	

	@Override
	public void init() {
		Notice tipNotice = Notice.createTipNotice("股市开市", "哈");
		TaskAction tipNoticeTA = TaskAction.createTimesTaskAction("tip",1);
		tipNoticeTA.setParams(tipNotice);
		tipNoticeTA.setAction(ACTION_NOTICE);
		mTaskQueue.addTask(tipNoticeTA);
		
		//添加通知
		addNoticeTask();
		
		//load stock
		InputStream in=this.getClass().getResourceAsStream(STOCK_FILE);
		if(in!=null){
			List<String> lines = IoUtil.lines(in);
			if(lines!=null){
				for(String line:lines){
					line=line.trim();
					if(line.startsWith("#")) continue;
					
					String params[] = line.split(",");
					if(params.length!=5) continue;
					
					String code=params[0].trim();
					String name=params[1].trim();
					double price=0;
					int total=0;
					String generator=params[4].trim();
					
					try{
						price=Double.parseDouble(params[2].trim());
						total=Integer.parseInt(params[3].trim());
					}catch(Exception e){
						log.warn("stocks.txt wrong params",e);
					}
					int priceInt = (int) (price*100);
					if(total<=100) total=100;
					if(priceInt<=0) priceInt=100;
					
					ITradeInfoGenerator tradeGenerator=null;
					if("crazy".equals(generator)){
						tradeGenerator=new CrazyTradeInfoGenerator();
					}else if("ten".equals(generator)){
						tradeGenerator=new TenTradeInfoGenerator();
					}else{
						tradeGenerator=new RandomTradeInfoGenerator();
					}
					
					Stock stock = new Stock(code,name,total,priceInt,tradeGenerator);
					stock.setUpdateTime(1000);
					this.addStock(stock);
				}
			}
		}
		
		//添加计算指数的时间
		addCaculateIndexTask();
		
		//大盘信息
		addMarketTask();
	}

	@Override
	public void run() {
		mMarketThread.start();

	}

	@Override
	public void destroy() {
		mIsRunning=false;
		try {
			mMarketThread.join();
		} catch (InterruptedException e) {
		}
	}

	class MarketThread extends Thread{
		@Override
		public void run() {
			mIsRunning=true;
			//run task
			long waitTime=0;
			long taskWaitTime=0;
			long nextTime=0;
			boolean runResult=false;
			
			while(mIsRunning){
				waitTime=REST_TIME;
				
				if(mTaskQueue.size()>0){
					nextTime=mTaskQueue.nextTime();
					if(nextTime!=-1){
						taskWaitTime=nextTime-System.currentTimeMillis();
						waitTime=Math.min(REST_TIME, taskWaitTime);
					}
					
					if(waitTime<=0){
						//run task
						TaskAction ta = mTaskQueue.removeTask();
						if(ta!=null){
							//log.debug("run task name={}",ta.getName());
							
							runResult=runTask(ta);
							if(runResult && ta.next()){
								//run again
								mTaskQueue.addTask(ta);
							}
						}
					}
				}
				
				if(waitTime>0){
					//sleep
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						
					}
				}
			}
		}
	}
	
	private boolean runTask(TaskAction ta){
		if(ta==null) return false;
		
		int action = ta.getAction();
		if(action==ACTION_NOTICE){
			mMsgPublicer.publicNotice((Notice) ta.getParams());
			return true;
		}else if(action==ACTION_STOCK){
			String code = (String) ta.getParams();
			Stock stock=mStockMap.get(code);
			if(stock==null) return false;
			
			stock.generateTradeInfo();
			mMsgPublicer.publicStockTradeInfo(mMarketInfo,stock);
			return true;
		}else if(action==ACTION_INDEX){
			caculateShanghaiMarketInfo();
			return true;
		}else if(action==ACTION_MARKET){
			Collection<Stock> collection = mStockMap.values();
			mMsgPublicer.publicMarketInfo(mMarketInfo, collection);
			return true;
		}
		
		return false;
	}
	
	//计算大盘
	public void caculateShanghaiMarketInfo(){
		Collection<Stock> collection = mStockMap.values();
		Iterator<Stock> iterator = collection.iterator();
		long oldValue=0;
		long value=0;
		Stock stock=null;
		while(iterator.hasNext()){
			stock = iterator.next();
			oldValue+=stock.getStartPrice()*stock.getTotal();
			value+=stock.getPrice()*stock.getTotal();
		}
		
		double newIndex = mMarketInfo.getStartShanghaiIndex()*value/oldValue;
		mMarketInfo.setShanghaiIndex(newIndex);
		
		mMarketInfo.setIndex(mMarketInfo.getIndex()+1);
	}
}
