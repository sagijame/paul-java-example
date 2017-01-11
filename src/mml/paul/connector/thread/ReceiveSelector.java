package mml.paul.connector.thread;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Vector;

import mml.paul.connector.Connection;
import mml.paul.connector.queue.Job;
import mml.paul.connector.queue.JobEventQueue;
import mml.paul.connector.queue.Log;

public class ReceiveSelector extends Thread {

	private Selector mSelector = null;
	private String name = "ReceiveSelector";
	
	private Vector<SocketChannel> mNewConnection = new Vector<SocketChannel>();
			
	public ReceiveSelector(int index) {		
		setName(name + (index+1));
		init();
	}
	
	private void init() {
		if ( mSelector == null ) {
			try {
				mSelector = Selector.open();
			} catch ( IOException e ) {
				Log.E(getName(), "init()", e.toString());
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void start() {
		init();
		Log.N(getName(), "Started.");		
		super.start();
	}
	
	public void close() {
		try {
			if ( mSelector != null ) {
				Iterator<SelectionKey> keys = mSelector.keys().iterator();
				while ( keys.hasNext() ) {
					SelectionKey key = keys.next();
					SocketChannel sc = (SocketChannel) key.channel();
					key.cancel();
					sc.close();					
				}
				mSelector.close();
				mSelector = null;				
			}
		} catch (IOException e) {
			e.printStackTrace();			
		}
	}
	
	@Override
	public void run() {
		try {
			while ( !Thread.currentThread().isInterrupted() ) {				
				processNewConnection();
				int keysReady = mSelector.select(1000);
				if ( keysReady > 0 )
					processRequest();
			}	
		} catch ( InterruptedException e ) {
			if ( Thread.currentThread().isInterrupted() )
				Log.W(getName(), "run()", "Interrupted");	
			else 
				Log.E(getName(), "run()", e.getMessage());
		} catch ( IOException e ) {
			Log.E(getName(), "run()", e.toString());
		}
	}
	
	private synchronized void processNewConnection() throws ClosedChannelException {
		synchronized ( mNewConnection ) {
			Iterator<SocketChannel> iter = mNewConnection.iterator();
			while ( iter.hasNext() ) {
				SocketChannel sc = (SocketChannel) iter.next();
				sc.register(mSelector, SelectionKey.OP_READ);
				Connection.getInstance().add(sc);
			}
			mNewConnection.clear();
		}
	}
	
	private void processRequest() throws InterruptedException, IOException {		
		Iterator<SelectionKey> iter = mSelector.selectedKeys().iterator();		
		while ( iter.hasNext() ) {			
			SelectionKey key = (SelectionKey) iter.next();	
			pushMyJob((SocketChannel)key.channel());
			Thread.sleep(1);
			iter.remove();
		}
	}
	
	private void pushMyJob(SocketChannel sc) {
		Job job = new Job(Job.EVENT_RECEIVE);
		job.put(Job.KEY_CHANNEL, sc);
		JobEventQueue.getInstance().push(job);
	}

	public void registConnection(SocketChannel sc) {
		mNewConnection.add(sc);
	}
	
	public Selector getSelector() {
		return mSelector;
	}
	
}
