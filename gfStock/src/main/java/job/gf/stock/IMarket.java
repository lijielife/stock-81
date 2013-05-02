package job.gf.stock;

import job.gf.stock.msg.IMsgPublicer;

/**
 * 股票市场接口
 * 
 * @author ouotuo
 * 
 */
public interface IMarket {
	// 消息发布者
	public void setIMsgPublicer(IMsgPublicer publicer);
	
	//添加股票
	public void addStock(Stock stock);
	
	//删除股票
	public boolean removeStock(String code);
	
	//停止股票
	public boolean stopStock(String code);
	
	//初始化
	public void init();
	
	//运行
	public void run();
	
	//停止
	public void destroy();
	
	//发布消息
	public void broadcastNotice(String title);
}
