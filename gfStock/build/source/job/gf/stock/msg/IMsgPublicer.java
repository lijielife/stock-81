package job.gf.stock.msg;

import java.util.Collection;

import job.gf.stock.MarketInfo;
import job.gf.stock.Stock;

/**
 * 股票行情信息发布接口
 * 
 * @author ouotuo
 * 
 */
public interface IMsgPublicer {
	// 发布通告等
	public void publicNotice(Notice notice);

	// 发布stock交易信息
	public void publicStockTradeInfo(MarketInfo marketInfo,Stock stock);

	// 发布大盘指数信息
	public void publicMarketInfo(MarketInfo marketInfo,Collection<Stock> collection);

}
