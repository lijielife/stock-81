package job.gf.stock;

import job.gf.stock.generate.ITradeInfoGenerator;
import job.gf.stock.generate.StockTradeInfo;

/**
 * 股票信息
 * 
 * @author ouotuo
 * 
 */
public class Stock {
	// 股票代码
	private String code;
	// 股票名字
	private String name;
	// 流通总量
	private int total = 1000;
	// 当前价格
	private int price = 1000;
	// 最高价格
	private int highPrice;
	// 最低价格
	private int lowPrice;
	// 开盘价格
	private int startPrice = 1000;
	// 现在的行情交易信息
	private StockTradeInfo currentTradeInfo;
	// 行情产生器
	private ITradeInfoGenerator tradeGenerator;
	// 更新频率 ms为单位
	private long updateTime = 5000;//5s
	
	public Stock(String code,String name,int total,int price,ITradeInfoGenerator tradeGenerator){
		this.code=code;
		this.name=name;
		this.total=total;
		this.price=price;
		highPrice=price;
		lowPrice=price;
		startPrice=price;
		this.tradeGenerator=tradeGenerator;
		
		currentTradeInfo=new StockTradeInfo();
		currentTradeInfo.setCurrentPrice(startPrice);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(int highPrice) {
		this.highPrice = highPrice;
	}

	public int getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(int lowPrice) {
		this.lowPrice = lowPrice;
	}

	public int getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(int startPrice) {
		this.startPrice = startPrice;
	}

	public StockTradeInfo getCurrentTradeInfo() {
		return currentTradeInfo;
	}

	public void setCurrentTradeInfo(StockTradeInfo currentTradeInfo) {
		this.currentTradeInfo = currentTradeInfo;
	}


	public ITradeInfoGenerator getTradeGenerator() {
		return tradeGenerator;
	}

	public void setTradeGenerator(ITradeInfoGenerator tradeGenerator) {
		this.tradeGenerator = tradeGenerator;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public void generateTradeInfo(){
		currentTradeInfo=tradeGenerator.generate(this);
		currentTradeInfo.setIndex(currentTradeInfo.getIndex()+1);
		this.price=currentTradeInfo.getCurrentPrice();
		
		if(price>highPrice){
			highPrice=price;
		}else if(price<lowPrice){
			lowPrice=price;
		}
		
	}
}
