package job.gf.simplejms.server;

import java.util.List;

public interface ITagSubscribeContainer {
	public List<String> noticeTag(TagMsg msg);
	
	public void subscribeTag(int tag,String subId);
	
	public void unSubscribeTag(int tag,String subId);
	
	public List<TagMsg> consumeTagMsg(String subId);
	
	public void removeSubscriber(String subId);
}
