package job.gf.simplejms.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import job.gf.simplejms.server.protocol.MsgFrame;
import job.gf.simplejms.server.protocol.MsgFrameParser;
import job.gf.simplejms.util.ConvertUtil;
import job.gf.util.IoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagSubscribeServer {
	static final String DEFAULT_CFG_FILE="/jmsServer.cnf";
	static final String DEFAULT_CFG_HOST="host";
	static final String DEFAULT_CFG_PORT="port";
	static final String DEFAULT_HOST="127.0.0.1";
	static final int DEFAULT_PORT=8889;
	
	Logger mLog = LoggerFactory.getLogger(TagSubscribeServer.class);

	static final int READ_BUFFER_SIZE = 8192;
	ByteBuffer mReadBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);

	ITagSubscribeContainer mTagSubscribeContainer = null;
	Selector mSelector;

	Map<String, MsgFrameParser> mChannelParser;

	int mPort = DEFAULT_PORT;
	String mHost = DEFAULT_HOST;
	
	ServerSocketChannel mServerSocketChannel=null;

	Map<String, SocketChannel> mChannelMap;
	
	boolean mIsRunning=false;
	ServerThread mServerThead = new ServerThread();

	private String getChannelSubId(SocketChannel channel) {
		return channel.toString();
	}

	// create socket
	private void accept(SelectionKey key) {
		ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
		SocketChannel channel=null;
		try {
			channel = serverChannel.accept();

			channel.configureBlocking(false);

			Socket socket = channel.socket();
			SocketAddress remoteAddr = socket.getRemoteSocketAddress();
			mLog.info("connection open,remoteAddr={}", remoteAddr);

			// register channel with selector for further IO
			channel.register(mSelector, SelectionKey.OP_READ);
		} catch (IOException e) {
			mLog.error("accept",e);
			return;
		}

		// create parser
		String subId = getChannelSubId(channel);
		mChannelParser.put(subId, new MsgFrameParser());
		mChannelMap.put(subId, channel);
	}

	// read msg
	private void read(SelectionKey key) {
		SocketChannel channel = (SocketChannel) key.channel();

		String subId = getChannelSubId(channel);
		int numRead = -1;
		MsgFrameParser frameParser = mChannelParser.get(subId);

		while (true) {
			mReadBuffer.clear();
			try {
				numRead = channel.read(mReadBuffer);
			} catch (IOException e) {
				//mLog.error("exception when read", e);
				numRead = -1;
			}

			if (numRead == -1) {
				// close by client
				// remove public subscribe
				closeChannel(channel);
				key.cancel();
				return;
			}

			if (numRead == 0)
				break;

			byte[] data = new byte[numRead];
			System.arraycopy(mReadBuffer.array(), 0, data, 0, numRead);

			frameParser.setReadData(data);
			MsgFrame frame = null;
			while ((frame = frameParser.read()) != null) {
				readFrame(subId, frame);
			}
		}
	}

	private void closeChannel(SocketChannel channel) {
		if (channel == null)
			return;
		String subId = getChannelSubId(channel);

		// close socket
		Socket socket = channel.socket();
		SocketAddress remoteAddr = socket.getRemoteSocketAddress();
		mLog.info("connection close, remoteAddr={}", remoteAddr);

		try {
			channel.close();
		} catch (IOException e) {
			mLog.error("closeChannel", e);
		}

		mTagSubscribeContainer.removeSubscriber(subId);
		mChannelParser.remove(subId);
		mChannelMap.remove(subId);
	}

	private void readFrame(String subId, MsgFrame frame) {
		TagMsg msg = new TagMsg(frame);
		if (msg.isOk()) {
			// public
			if (msg.isTypePublic()) {
				List<String> noticeList = mTagSubscribeContainer.noticeTag(msg);

				if (noticeList != null && noticeList.size() > 0) {
					for (String id : noticeList) {
						SocketChannel channel = mChannelMap.get(id);
						if (channel != null) {
							// registry read write
							try {
								channel.register(mSelector,
										SelectionKey.OP_READ
												| SelectionKey.OP_WRITE);
							} catch (ClosedChannelException e) {
								closeChannel(channel);
							}
						}
					}
				}
			} else if (msg.isTypeSubscribe()) {
				mTagSubscribeContainer.subscribeTag(msg.getTag(), subId);
			}else if(msg.isTypeUnSubscribe()){
				mTagSubscribeContainer.unSubscribeTag(msg.getTag(), subId);
			} 
			else {
				mLog.warn("unknown frame type={}", msg.getType());
			}
		} else {
			mLog.warn("not a TagMsg frame");
		}
	}

	private void write(SelectionKey key) {
		SocketChannel channel = (SocketChannel) key.channel();
		String subId = getChannelSubId(channel);

		List<TagMsg> data = mTagSubscribeContainer.consumeTagMsg(subId);
		if (data != null) {
			for (TagMsg tm : data) {
				// 先写0
				try {
					channel.write(ByteBuffer.wrap(MsgFrameParser.FRAME_BEGIN));

					// 写长度
					byte[] bs = ConvertUtil.intToByte(tm.getDataLen() + 4);
					channel.write(ByteBuffer.wrap(bs));

					// tag
					channel.write(ByteBuffer.wrap(TagSubscribeUtil
							.msgTagToByte(tm.getTag())));

					// 再写数据
					channel.write(ByteBuffer.wrap(tm.getData()));
				} catch (IOException e) {
					mLog.error("write channel", e);
				}
			}
		}

		try {
			channel.register(mSelector, SelectionKey.OP_READ);
		} catch (ClosedChannelException e) {
			closeChannel(channel);
			key.cancel();
		}
	}

	public boolean startServer() {
		mLog.info("jmsServer start");
		
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
		
		mLog.info("listen host={},port={}", mHost, mPort);

		mTagSubscribeContainer = new SimpleTagSubscribeContainer();
		mChannelParser = new HashMap<String, MsgFrameParser>(16);
		mChannelMap = new HashMap<String, SocketChannel>(16);

		try {
			mSelector = Selector.open();

			// 创建一个用于建立连接的ServerSocketChannel
			mServerSocketChannel = ServerSocketChannel
					.open();
			InetSocketAddress address = new InetSocketAddress(mHost, mPort);
			mServerSocketChannel.socket().bind(address);
			mServerSocketChannel.configureBlocking(false);
			mServerSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT); // 注册Accept事件

		} catch (IOException e1) {
			mLog.error("error when start server", e1);
			return false;
		}
		
		mServerThead.start();
		return true;
	}
	
	public void stopServer(){
		mIsRunning=false;
		
		try{
			mSelector.wakeup();
		}catch(Exception e){
			
		}
		
		try {
			mServerThead.join();
		} catch (InterruptedException e1) {
			mLog.warn("error when jon server thread",e1);
		}
		
		if(mSelector!=null){
			try {
				mSelector.close();
			} catch (IOException e) {
				mLog.warn("error when close server select",e);
			}
		}
		
		if(mServerSocketChannel!=null){
			try {
				mServerSocketChannel.close();
			} catch (IOException e) {
				mLog.warn("error when close server socket",e);
			}
		}
		
		if(mChannelMap!=null){
			mChannelMap.clear();
		}
	}
	
	class ServerThread extends Thread{

		@Override
		public void run() {
			mIsRunning=true;

			while (mIsRunning) {
				int n = 0;
				try {
					n = mSelector.select();
				} catch (IOException e1) {
					mLog.error("error when selector.select", e1);
				}

				if (n == 0) {
					try {
						Thread.sleep(3000);
						continue;
					} catch (InterruptedException e) {
						mLog.error("error when sleep", e);
					}
				}

				Set<SelectionKey> selectedKeys = mSelector.selectedKeys();

				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

				while (keyIterator.hasNext()) {
					SelectionKey selectionKey = keyIterator.next();
					keyIterator.remove();

					if (selectionKey.isAcceptable()) {
						accept(selectionKey);
					} else if (selectionKey.isReadable()) {
						read(selectionKey);
					} else if (selectionKey.isWritable()) {
						write(selectionKey);
					}
				}
			}
		}
		
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new TagSubscribeServer().startServer();
	}

}
