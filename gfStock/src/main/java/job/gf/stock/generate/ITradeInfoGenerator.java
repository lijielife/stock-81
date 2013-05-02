package job.gf.stock.generate;

import job.gf.stock.Stock;

/**
 * 行情发生器产生接口
 * @author ouotuo
 *
 */
public interface ITradeInfoGenerator {
	//发生器名字
	public String getName();
	
	/**
	 * 根据股票信息和行情,产生下一次的行情信息
	 */
	public StockTradeInfo generate(Stock stock);
}
