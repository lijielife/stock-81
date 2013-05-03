package job.gf.simplejms.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import job.gf.simplejms.server.protocol.MsgFrame;
import job.gf.simplejms.server.protocol.MsgFrameParser;
import job.gf.simplejms.util.ConvertUtil;
import job.gf.util.IoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTagSubscribeClient implements ITagSubscribeClient {
	Logger mLog = LoggerFactory.getLogger(SimpleTagSubscribeClient.class);
	
	static final String DEFAULT_CFG_FILE="/jmsClient.cnf";
	static final String DEFAULT_CFG_HOST="host";
	static final String DEFAULT_CFG_PORT="port";
	static final String DEFAULT_HOST="127.0.0.1";
	static final int DEFAULT_PORT=8889;

	String mHost=DEFAULT_HOST;
	int mPort=DEFAULT_PORT;
	List<Integer> mTagList = new ArrayList<Integer>(4);

	// 操持发送消息用
	Queue<RequestTagMsg> mMsgQueue;
	ClientThread mThread;

	OnNoticeListener mOnNoticeListener;

	public SimpleTagSubscribeClient() {
		mMsgQueue = new ConcurrentLinkedQueue<RequestTagMsg>();
		mHost = "127.0.0.1";
		mPort = 8888;
		
		mThread=new ClientThread();
	}

	public String getmHost() {
		return mHost;
	}

	public void setmHost(String mHost) {
		this.mHost = mHost;
	}

	public int getmPort() {
		return mPort;
	}

	public void setmPort(int mPort) {
		this.mPort = mPort;
	}

	@Override
	public void noticeTag(RequestTagMsg tagMsg) {
		tagMsg.setType(RequestTagMsg.TYPE_PUBLIC);
		mMsgQueue.add(tagMsg);
		mThread.notifyWrite();
	}

	@Override
	public void unSubscribeTag(int tag) {
		mLog.info("unSubscribe tag=",tag);
		if(mTagList.contains(tag)){
			mTagList.remove(tag);
		}
		
		RequestTagMsg tm = new RequestTagMsg();
		tm.setType(RequestTagMsg.TYPE_UNSUBSCRIBE);
		tm.setTag(tag);
		mMsgQueue.add(tm);
		mThread.notifyWrite();
	}
	
	
	@Override
	public void subscribeTag(int tag) {
		mLog.info("subscribe tag=",tag);
		if(!mTagList.contains(tag)){
			mTagList.add(tag);
		}
		
		RequestTagMsg tm = new RequestTagMsg();
		tm.setType(RequestTagMsg.TYPE_SUBSCRIBE);
		tm.setTag(tag);
		mMsgQueue.add(tm);
		mThread.notifyWrite();
	}

	@Override
	public void start() {
		mLog.info("jmsClient start");
		
		Map<String,String> propMap = IoUtil.loadProp(this.getClass().getResourceAsStream(DEFAULT_CFG_FILE));
		if(propMap!=null){
			String host=propMap.get(DEFAULT_CFG_HOST);
			String port=propMap.get(DEFAULT_CFG_PORT);
			if(host!=null){
				mHost=host;
			}
			if(port!=null){
				mPort=Integer.parseInt(port);
			}
			
		}
		
		mLog.info("connect host={},port={}", mHost, mPort);
		
		mThread.start();
	}

	class ClientThread extends Thread {
		// channel
		ByteBuffer mBuffer = ByteBuffer.allocate(8192);
		Selector mSelector = null;
		SocketChannel mSocketChannel = null;
		
		long mErrorSleepTime = 3000;// 1s
		long mSleepTime = 1000;// 1s
		MsgFrameParser mFrameParser = new MsgFrameParser();
		boolean mIsDestory=false;
		
		static final int STATUS_INIT=0;
		static final int STATUS_CONNECTING=2;
		static final int STATUS_CONNECTED=3;
		int connectStatus=STATUS_INIT;
		
		
		public void destoryThread(){
			mIsDestory=true;
		}

		@Override
		public void run() {
			while (true) {
				// block
				int n = 0;
				try {
					if(connectStatus==STATUS_CONNECTED|| connectStatus==STATUS_CONNECTING && mSelector!=null)
						n = mSelector.select();
				} catch (Exception e) {
					mLog.error("Selector.select", e);
					closeSocket();
				}
				
				if (mIsDestory) {
					mLog.info("destory the client");
					closeSocket();
					break;
				}
				

				if(n==0){
					if (connectStatus==STATUS_INIT) {
						mLog.info("try to connect to Server");

						connectSocket();
					}else if(connectStatus==STATUS_CONNECTED ){
						if(mSocketChannel!=null && mSocketChannel.isConnected()){
							//connect is ok
							if(mMsgQueue.size() > 0){
								// registry
								try {
									mSocketChannel.register(mSelector,
											SelectionKey.OP_READ
													| SelectionKey.OP_WRITE);
								} catch (ClosedChannelException e) {
									closeSocket();
								}
							}
						}else{
							//not connect
							closeSocket();
						}
					}else if(connectStatus==STATUS_CONNECTING){
						closeSocket();
					}else{
						try {
							Thread.sleep(mSleepTime);
						} catch (InterruptedException e) {
						}
					}
					
					continue;
				}
				

				Set<SelectionKey> selectionKeys = mSelector.selectedKeys();
				if(selectionKeys.isEmpty()) continue;
				
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				while (iterator.hasNext()) {
					SelectionKey selectKey = iterator.next();
					iterator.remove();

					// read
					if(selectKey.isConnectable()){
						connectServer(selectKey);
					}else if (selectKey.isReadable()) {
						readData(selectKey);
					} else if (selectKey.isWritable()) {
						writeData(selectKey);
					}
				}
			}
			
			mLog.info("client destory");
		}
		
		private void connectServer(SelectionKey selectKey){
			SocketChannel channel = (SocketChannel) selectKey.channel();
			
				try {
					boolean result = channel.finishConnect();
					
					if(result){
						channel.register(mSelector,
								SelectionKey.OP_READ);
						
						for(Integer tag:mTagList){
							subscribeTag(tag);
						}
						mLog.info("connect sucess");
						
						connectStatus=STATUS_CONNECTED;
					}else{
						closeSocket();
					}
				} catch (IOException e) {
					mLog.error("error when finishConnect", e);
					mLog.error("can not connect to server");
					
					try {
						Thread.sleep(mErrorSleepTime);
					} catch (InterruptedException e1) {
					}
					closeSocket();
				}  
				
				//
		}

		private void readData(SelectionKey selectKey) {
			SocketChannel channel = (SocketChannel) selectKey.channel();

			int numRead = -1;

			while (true) {
				mBuffer.clear();
				try {
					numRead = channel.read(mBuffer);
				} catch (IOException e) {
					//mLog.error("exception when read", e);
				}

				if (numRead == -1) {
					// close by server
					mLog.info("connect is close by Server");
					closeSocket();
					return;
				}

				if (numRead == 0)
					break;

				byte[] data = new byte[numRead];
				System.arraycopy(mBuffer.array(), 0, data, 0, numRead);

				mFrameParser.setReadData(data);
				MsgFrame frame = null;
				while ((frame = mFrameParser.read()) != null) {
					readFrame(frame);
				}
			}
		}

		private void readFrame(MsgFrame frame) {
			ClientTagMsg msg = new ClientTagMsg(frame);
			if (msg.isOk()) {
				// public
				if(mOnNoticeListener!=null){
					mOnNoticeListener.onNoticeTag(msg);
				}
			} else {
				mLog.warn("wrong frame");
			}
		}

		private void writeData(SelectionKey selectKey) {
			SocketChannel channel = (SocketChannel) selectKey.channel();

			if (mMsgQueue != null && mMsgQueue.size()>0) {
				Iterator<RequestTagMsg> iter = mMsgQueue.iterator();
				while(iter.hasNext()){
					RequestTagMsg tm = iter.next();
					iter.remove();
					
					try {
						// 先写0
						channel.write(ByteBuffer.wrap(MsgFrameParser.FRAME_BEGIN));
						
						// 再写长度+4+4
						byte[] bs = ConvertUtil.intToByte(tm.getDataLen()+8);
						channel.write(ByteBuffer.wrap(bs));
						
						//类型
						bs = ConvertUtil.intToByte(tm.getType());
						channel.write(ByteBuffer.wrap(bs));
						//tag
						bs = ConvertUtil.intToByte(tm.getTag());
						channel.write(ByteBuffer.wrap(bs));
						
						// 再写数据
						if(tm.getDataLen()>0)
							channel.write(ByteBuffer.wrap(tm.getData()));
					} catch (IOException e) {
						mLog.error("error when write data",e);
					}
				}
			}
			try {
				channel.register(mSelector,
						SelectionKey.OP_READ);
			} catch (ClosedChannelException e) {
				//
				closeSocket();
			}
		}

		public void notifyWrite() {
			if(mSelector!=null)
				mSelector.wakeup();
		}

		private void connectSocket() {
			closeSocket();
			mLog.info("try to connect to server,address={},port={}", mHost, mPort);
			try {
				mSelector = Selector.open();
				mSocketChannel = SocketChannel.open();
				mSocketChannel.configureBlocking(false);
				mSocketChannel.connect(new InetSocketAddress(mHost, mPort));
				/*
				if(!result){
					if (mSocketChannel.isConnectionPending()) {
						if(mSocketChannel.finishConnect()){
							mHasConnect = false;;
						}
					}
				}*/
				
				mSocketChannel.register(mSelector, SelectionKey.OP_CONNECT);
				connectStatus=STATUS_CONNECTING;
			} catch (IOException e1) {
				mLog.error("error when connectSocket", e1);
				
				closeSocket();
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}

		private void closeSocket() {
			connectStatus=STATUS_INIT;
			mLog.info("close socket");

			if (mSelector != null) {
				try {
					mSelector.close();
				} catch (IOException e) {
				}
			}
			if (mSocketChannel != null) {
				try {
					mSocketChannel.close();
				} catch (IOException e) {
				}
			}
			
			mFrameParser.clear();
		}

	}

	@Override
	public void destroy() {
		mThread.destoryThread();
		try {
			mThread.notifyWrite();
			mThread.join();
		} catch (InterruptedException e) {
			mLog.error("error when mThread.join()", e);
		}
	}
	
	@Override
	public void setOnNoticeListener(OnNoticeListener mOnNoticeListener) {
		this.mOnNoticeListener=mOnNoticeListener;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleTagSubscribeClient client = new SimpleTagSubscribeClient();
		client.start();
		client.subscribeTag(34);
		client.setOnNoticeListener(new OnNoticeListener(){

			@Override
			public void onNoticeTag(ClientTagMsg tm) {
				byte[] data = tm.getData();
				String str = new String(data);
			}
		});
		
		RequestTagMsg tagMsg=new RequestTagMsg();
		tagMsg.setTag(34);
		tagMsg.setType(RequestTagMsg.TYPE_PUBLIC);
		tagMsg.setData("abc".getBytes());
		client.noticeTag(tagMsg);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//client.destroy();

	}
}
