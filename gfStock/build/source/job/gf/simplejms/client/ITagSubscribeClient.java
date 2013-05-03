package job.gf.simplejms.client;


public interface ITagSubscribeClient {
	public void noticeTag(RequestTagMsg tagMsg);
	public void subscribeTag(int tag);
	public void unSubscribeTag(int tag);
	public void start();
	public void destroy();
	public void setOnNoticeListener(OnNoticeListener mOnNoticeListener);
	
	public static interface OnNoticeListener{
		public void onNoticeTag(ClientTagMsg tm);
	}
}
