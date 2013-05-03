package job.gf.stock;

import job.gf.stock.msg.IMsgPublicer;
import job.gf.stock.msg.Notice;

/**
 * 股票市场抽象
 * @author ouotuo
 *
 */
public abstract class AbstractMarket implements IMarket{
	//用来发布消息
	protected IMsgPublicer mMsgPublicer;
	
	// 消息发布者
	public void setIMsgPublicer(IMsgPublicer publicer){
		mMsgPublicer=publicer;
	}
	
	public void broadcastNotice(String title){
		if(mMsgPublicer!=null){
			Notice notice = Notice.createTipNotice(title, "test");
			mMsgPublicer.publicNotice(notice);
		}
	}
}
