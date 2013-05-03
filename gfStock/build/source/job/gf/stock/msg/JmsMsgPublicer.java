package job.gf.stock.msg;

import java.util.Collection;

import job.gf.simplejms.client.ITagSubscribeClient;
import job.gf.simplejms.client.RequestTagMsg;
import job.gf.stock.MarketInfo;
import job.gf.stock.Stock;
import job.gf.stock.generate.StockTradeInfo;
import job.gf.stock.generate.TradeInfo;

import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;
import org.bson.types.BasicBSONList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * jms信息发布者
 * @author ouotuo
 *
 */
public class JmsMsgPublicer implements IMsgPublicer {
	Logger log = LoggerFactory.getLogger(getClass());
	
	//jms的client
	ITagSubscribeClient client;
	BasicBSONEncoder encoder = new BasicBSONEncoder();
	
	public JmsMsgPublicer(ITagSubscribeClient client){
		this.client=client;
	}

	@Override
	public void publicNotice(Notice notice) {
		BasicBSONObject bson  = new BasicBSONObject();
		bson.append("msgId", "notice");
		bson.append("type", notice.getType());
		bson.append("title", notice.getTitle());
		bson.append("content", notice.getContent());
		bson.append("publicTime", System.currentTimeMillis());
		byte[] bs = encoder.encode(bson);
		
		RequestTagMsg tagMsg = new RequestTagMsg();
		tagMsg.setTag(PublicerConstant.TAG_NOTICE);
		tagMsg.setType(RequestTagMsg.TYPE_PUBLIC);
		tagMsg.setData(bs);
		
		client.noticeTag(tagMsg);
	}

	@Override
	public void publicStockTradeInfo(MarketInfo marketInfo,Stock stock) {
		//log.debug("publicStockTradeInfo,stock code={}",stock.getCode());
		BasicBSONObject bson  = new BasicBSONObject();
		bson.append("msgId", "stock");
		bson.append("action", "list");
		
		bson.append("code", stock.getCode());
		bson.append("name", stock.getName());
		bson.append("price", stock.getPrice());
		bson.append("total", stock.getTotal());
		bson.append("highPrice", stock.getHighPrice());
		bson.append("lowPrice", stock.getLowPrice());
		bson.append("startPrice", stock.getStartPrice());
		bson.append("priceTime", System.currentTimeMillis());
		
		bson.append("startShanghaiIndex", marketInfo.getStartShanghaiIndex());
		bson.append("shanghaiIndex", marketInfo.getShanghaiIndex());
		
		//buy
		StockTradeInfo sti = stock.getCurrentTradeInfo();
		bson.append("index", sti.getIndex());
		BasicBSONList buyListBson = new BasicBSONList();
		for(TradeInfo tr:sti.getBuyList()){
			BasicBSONObject bo = new BasicBSONObject();
			bo.append("num", tr.getNum());
			bo.append("price", tr.getPrice());
			
			buyListBson.add(bo);
		}
		bson.append("buyList", buyListBson);
		
		BasicBSONList saleListBson = new BasicBSONList();
		for(TradeInfo tr:sti.getSaleList()){
			BasicBSONObject bo = new BasicBSONObject();
			bo.append("num", tr.getNum());
			bo.append("price", tr.getPrice());
			
			saleListBson.add(bo);
		}
		bson.append("saleList", saleListBson);
		
		byte[] bs = encoder.encode(bson);
		
		RequestTagMsg tagMsg = new RequestTagMsg();
		tagMsg.setTag(PublicerConstant.TAG_STOCK);
		tagMsg.setType(RequestTagMsg.TYPE_PUBLIC);
		tagMsg.setData(bs);
		
		client.noticeTag(tagMsg);

	}

	@Override
	public void publicMarketInfo(MarketInfo marketInfo,Collection<Stock> collection) {
		BasicBSONObject bson  = new BasicBSONObject();
		bson.append("msgId", "market");
		
		bson.append("shanghaiIndex", marketInfo.getShanghaiIndex());
		bson.append("highShanghaiIndex", marketInfo.getHighShanghaiIndex());
		bson.append("lowShangghaiIndex", marketInfo.getLowShangghaiIndex());
		bson.append("startShanghaiIndex", marketInfo.getStartShanghaiIndex());
		bson.append("publicTime", System.currentTimeMillis());
		bson.append("index", marketInfo.getIndex());
		
		//buy
		BasicBSONList stockListBson = new BasicBSONList();
		for(Stock stock:collection){
			BasicBSONObject bo = new BasicBSONObject();
			bo.append("code", stock.getCode());
			bo.append("name", stock.getName());
			bo.append("price", stock.getPrice());
			bo.append("startPrice", stock.getStartPrice());
			bo.append("highPrice", stock.getHighPrice());
			bo.append("lowPrice", stock.getLowPrice());
			
			stockListBson.add(bo);
		}
		bson.append("stockList", stockListBson);
		
		byte[] bs = encoder.encode(bson);
		
		RequestTagMsg tagMsg = new RequestTagMsg();
		tagMsg.setTag(PublicerConstant.TAG_MARKET);
		tagMsg.setType(RequestTagMsg.TYPE_PUBLIC);
		tagMsg.setData(bs);
		
		client.noticeTag(tagMsg);
	}

}
