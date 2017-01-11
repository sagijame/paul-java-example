package mml.paul.connector.thread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import mml.paul.connector.Connector;
import mml.paul.connector.queue.Job;
import mml.paul.connector.queue.JobEventQueue;
import mml.paul.connector.queue.Log;

public class AcceptSelector extends Thread {
	
	private Selector mSelector = null;
	private ServerSocketChannel mServerSocketChannel = null;
	
	private String name = "AcceptSelector";
	
	public AcceptSelector(int index) {
		setName(name + (index+1));
		init();
	}
	
	private void init() {
		try {
			if ( mSelector == null )				
			   mSelector = Selector.open();						
			if ( mServerSocketChannel == null ) {
				mServerSocketChannel = ServerSocketChannel.open();
				mServerSocketChannel.configureBlocking(false);
				mServerSocketChannel.socket().bind(new InetSocketAddress(Connector.HOST, Connector.PORT));
				mServerSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);
				Log.N(getName(), "Bounded to " + Connector.HOST + ".");
			}
		} catch ( IOException e ) {
			Log.E(getName(), "init()", e.toString());
		}
	}
	
	@Override
	public void start() {
		init();
		Log.N(getName(), "Started.");
		super.start();		
	}
	
	public void close() {
		if ( mSelector != null ) {
			Iterator<SelectionKey> keys = mSelector.keys().iterator();
			while ( keys.hasNext() ) {
				SelectionKey key = keys.next();
				key.cancel();
			}			
			try {
				mServerSocketChannel.close();
				mServerSocketChannel = null;
				mSelector.close();
				mSelector = null;
			} catch (IOException e) {
				e.printStackTrace();			
			}			
		}
	}
	
	@Override
	public void run() {		
		try {
			while ( !Thread.currentThread().isInterrupted() ) {									
				/*int keysReady = */mSelector.select();					
				acceptPendingConnections();
			}	
		} catch ( Exception e ) {
			if ( e instanceof InterruptedException ) {
				if ( Thread.currentThread().isInterrupted() )
					Log.W(getName(), "run()", "Interrupted.");
				else
					Log.E(getName(), "run()", e.getMessage());
			} else
				Log.E(getName(), "run()", e.getMessage());
		}
	}
	
	private void acceptPendingConnections() throws IOException {
		Iterator<SelectionKey> iter = mSelector.selectedKeys().iterator();
		while ( iter.hasNext() ) {
			SelectionKey key = (SelectionKey) iter.next();
			ServerSocketChannel readyChannel = (ServerSocketChannel) key.channel();
			SocketChannel sc = readyChannel.accept();
			Log.N(getName(), "Connected from " + sc.socket().getInetAddress().getHostAddress() + ":" + sc.socket().getPort() + ".");
			pushMyJob(sc);
			iter.remove();
		}
	}
	
	private void pushMyJob(SocketChannel sc) {
		Job job = new Job(Job.EVENT_ACCEPT);
		job.put(Job.KEY_CHANNEL, sc);
		JobEventQueue.getInstance().push(job);
	}
	
}
