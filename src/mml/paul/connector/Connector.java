package mml.paul.connector;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import mml.paul.connector.pool.ByteBufferPool;
import mml.paul.connector.pool.PoolManager;
import mml.paul.connector.pool.SelectorPool;
import mml.paul.connector.pool.ThreadPool;
import mml.paul.connector.queue.Log;
import mml.paul.connector.thread.ReceiveSelector;

public class Connector {
	
	public static final String TYPE_SERVER = "Server";
	public static final String TYPE_CLIENT = "Client";	
	
	public static final String HOST = "155.230.118.75";
	public static final int PORT = 9090;	
	
	private static String mType;
	
	private SelectorPool mAcceptSelectorPool = null;
	private SelectorPool mReceiveSelectorPool = null;
	
	private ByteBufferPool mByteBufferPool = null;
	
	private ThreadPool mAcceptorPool = null;
	private ThreadPool mReceiverPool = null;
	private ThreadPool mSenderPool = null;
	
	private boolean isRunning = false;
	
	private static SocketChannel mSocketChannel;
	
	public Connector(String type) {
		this(type, "");
	}
	
	public Connector(String type, String bufferFilePath) {
		mType = type;
		
		try {
			init(bufferFilePath);
		} catch (IOException e) {
			Log.E("Connector", "Connector()", e.toString());
			e.printStackTrace();
		}
	}
	
	private void init(String bufferFilePath) throws IOException {
		Log.N("Connector", mType + " Initializing...");
		
		if ( bufferFilePath == null )
			bufferFilePath = "";
		File bufferFile = new File(bufferFilePath + mType + "Buffer.tmp");
		if ( !bufferFile.exists() ) 
			bufferFile.createNewFile();		
		bufferFile.deleteOnExit();
		
		mByteBufferPool = new ByteBufferPool(20*1024, 40*2048, bufferFile);		
		PoolManager.registByteBufferPool(mByteBufferPool);
		
		if ( mType.equals(TYPE_SERVER) ) {
			mAcceptorPool = new ThreadPool(ThreadPool.ACCEPTOR);
			
			mAcceptSelectorPool = new SelectorPool(SelectorPool.ACCEPT_SELECTOR, 1);
			PoolManager.registAcceptSelectorPool(mAcceptSelectorPool);
		}
			
		mReceiverPool = new ThreadPool(ThreadPool.RECEIVER);
		mSenderPool = new ThreadPool(ThreadPool.SENDER);		
		
		mReceiveSelectorPool = new SelectorPool(SelectorPool.RECEIVE_SELECTOR, 1);	
		PoolManager.registReceiveSelectorPool(mReceiveSelectorPool);
		
		Log.N("Connector", mType + " Initialized.");
	}
	
	public void start() {		
		Log.N("Connector", mType + " Starting...");
		
		if ( mType.equals(TYPE_SERVER) )
			mAcceptorPool.startAll();
		mReceiverPool.startAll();
		mSenderPool.startAll();
		
		if ( mType.equals(TYPE_SERVER) )
			mAcceptSelectorPool.startAll();
		mReceiveSelectorPool.startAll();
		
		if ( mType.equals(TYPE_CLIENT) ) {
			try {
				mSocketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
				mSocketChannel.configureBlocking(false);
				ReceiveSelector rs = (ReceiveSelector)mReceiveSelectorPool.get();
				rs.registConnection(mSocketChannel);
			} catch (IOException e) {
				Log.E("Connector", "start()", e.toString());
			}
		}
		isRunning = true;
		
		Log.N("Connector", mType + " Started.");
	}
	
	public void stop() {
		Log.N("Connector", mType + " Stopping...");		
		
		if ( mType.equals(TYPE_SERVER) )
			mAcceptorPool.stopAll();
		mReceiverPool.stopAll();
		mSenderPool.stopAll();
		
		if ( mType.equals(TYPE_SERVER) )
			mAcceptSelectorPool.stopAll();
		mReceiveSelectorPool.stopAll();

		if ( mType.equals(TYPE_CLIENT) ) {
			try {
				if ( mSocketChannel != null ) {
					mSocketChannel.close();
					mSocketChannel = null;
				}
			} catch ( IOException e ) {
				Log.E("Connector", "stop()", e.toString());
			}
		}
		
		isRunning = false;
		Log.N("Connector", mType + "Stopped.");
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public static String getType() {
		return mType;
	}
	
	public static SocketChannel getSocketChannel() {
		return mSocketChannel;
	}
	
}
