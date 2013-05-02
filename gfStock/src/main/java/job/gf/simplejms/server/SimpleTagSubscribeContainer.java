package job.gf.simplejms.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTagSubscribeContainer implements ITagSubscribeContainer{
	Logger mLog = LoggerFactory.getLogger(SimpleTagSubscribeContainer.class);
	// tag --> subId
	Map<String,List<String>> mTagMap=new HashMap<String,List<String>>(16);

	//subId ---> tagMsg
	Map<String,List<TagMsg>> mTagMsgMap=new HashMap<String,List<TagMsg>>(16);

	private String tagToKey(int tag){
		return String.valueOf(tag);
	}
	

	@Override
	public List<String> noticeTag(TagMsg msg) {
		mLog.debug("noticeTag tag={}",msg.getTag());
		List<String> list = mTagMap.get(tagToKey(msg.getTag()));
		if(list!=null && list.size()>0){
			for(String subId:list){
				addNoticeTag(subId,msg);
			}
			List<String> ret = new ArrayList<String>(list);
			return ret;
		}
		
		return null;
	}
	
	private void addNoticeTag(String subId,TagMsg msg){
		List<TagMsg> list = mTagMsgMap.get(subId);
		if(list==null){
			list = new LinkedList<TagMsg>();
			mTagMsgMap.put(subId, list);
		}
		
		list.add(msg);
	}

	@Override
	public void subscribeTag(int tag, String subId) {
		mLog.debug("subId={} subscribe tag={}",subId,tag);
		String tagStr = tagToKey(tag);
		List<String> list = mTagMap.get(tagStr);
		if(list==null){
			list=new LinkedList<String>();
			mTagMap.put(tagStr, list);
		}
		
		if(!list.contains(subId)){
			list.add(subId);
		}
	}

	@Override
	public void unSubscribeTag(int tag, String subId) {
		mLog.debug("subId={} unSubscribe tag={}",subId,tag);
		String tagStr = tagToKey(tag);
		List<String> list = mTagMap.get(tagStr);
		if(list==null){
			return;
		}
		
		list.remove(subId);
	}

	@Override
	public List<TagMsg> consumeTagMsg(String subId) {
		List<TagMsg> list = mTagMsgMap.get(subId);
		
		mTagMsgMap.remove(subId);
		return list;
	}


	@Override
	public void removeSubscriber(String subId) {
		mLog.debug("removeSubscriber subId={}",subId);
		if(mTagMap.size()>0){
			Set<Entry<String, List<String>>> entrySet = mTagMap.entrySet();
			Iterator<Entry<String, List<String>>> iter = entrySet.iterator();
			while(iter.hasNext()){
				Entry<String, List<String>> entity = iter.next();
				entity.getValue().remove(subId);
			}
		}
		
		consumeTagMsg(subId);
	}
}
