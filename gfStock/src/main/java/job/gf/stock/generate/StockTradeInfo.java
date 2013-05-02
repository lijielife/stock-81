package job.gf.stock.generate;

import java.util.ArrayList;
import java.util.List;

/**
 * 股票行情信息
 * 
 * @author ouotuo
 * 
 */
public class StockTradeInfo {
	// 卖出列表
	private List<TradeInfo> saleList = new ArrayList<TradeInfo>(5);
	// 买入列表
	private List<TradeInfo> buyList = new ArrayList<TradeInfo>(5);
	// 现在价格
	private int currentPrice = 0;

	private long index=0;

	public List<TradeInfo> getSaleList() {
		return saleList;
	}

	public void setSaleList(List<TradeInfo> saleList) {
		this.saleList = saleList;
	}

	public List<TradeInfo> getBuyList() {
		return buyList;
	}

	public void setBuyList(List<TradeInfo> buyList) {
		this.buyList = buyList;
	}

	public int getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(int currentPrice) {
		this.currentPrice = currentPrice;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

}
