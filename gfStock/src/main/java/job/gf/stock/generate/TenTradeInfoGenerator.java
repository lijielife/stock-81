package job.gf.stock.generate;

import java.util.List;
import java.util.Random;

import job.gf.stock.Stock;

public class TenTradeInfoGenerator extends AbstractTradeInfoGenerator {
	Random random = new Random();
	
	public TenTradeInfoGenerator() {
		super("random");
	}

	@Override
	public StockTradeInfo generate(Stock stock) {
		StockTradeInfo sti = stock.getCurrentTradeInfo();
		
		//现在的价格
		int currentPrice = stock.getPrice();
		
		int div = random.nextInt(30);
		
		if(random.nextBoolean()){
			currentPrice+=div;
		}else{
			currentPrice-=div;
		}
		
		int divD=(int) (stock.getStartPrice()*0.1);
		int maxPrice = stock.getStartPrice()+divD;
		int minPrice = stock.getStartPrice()-divD;
		if(currentPrice<=0){
			currentPrice=1;
		}
		if(currentPrice>maxPrice) currentPrice=maxPrice;
		else if(currentPrice<minPrice)currentPrice=minPrice;
		
		//产生买家
		int buyNum=random.nextInt(4)+2;
		List<TradeInfo> buyList = sti.getBuyList();
		buyList.clear();
		
		int tiP=currentPrice;
		int tiN=0;
		int totalShou= stock.getTotal()/100;
		for(int i=0;i<buyNum;i++){
			tiP-=1;
			if(random.nextInt(10)>7){
				//2个数
				tiP-=1;
			}
			if(random.nextInt(10)>8){
				//2个数
				tiP-=1;
			}
			
			tiN = random.nextInt(totalShou)/2+1;
			
			TradeInfo ti = new TradeInfo();
			ti.setNum(tiN);
			ti.setPrice(tiP);
			buyList.add(ti);
		}
		
		//产生卖家
		tiP=currentPrice;
		int saleNum=random.nextInt(4)+2;
		List<TradeInfo> saleList = sti.getSaleList();
		saleList.clear();
		for(int i=0;i<saleNum;i++){
			tiP+=1;
			if(random.nextInt(10)>7){
				//2个数
				tiP+=1;
			}
			if(random.nextInt(10)>8){
				//2个数
				tiP+=1;
			}
			
			if(tiP<=0) continue;
			
			tiN = random.nextInt(totalShou)/2+1;
			
			TradeInfo ti = new TradeInfo();
			ti.setNum(tiN);
			ti.setPrice(tiP);
			saleList.add(ti);
		}
		
		sti.setCurrentPrice(currentPrice);
		return sti;
	}

}
