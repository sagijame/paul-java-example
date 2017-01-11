package mml.paul.connector.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mml.paul.connector.Connector;
import mml.paul.connector.queue.Log;
import mml.paul.connector.thread.Acceptor;
import mml.paul.connector.thread.Receiver;
import mml.paul.connector.thread.Sender;

public class ThreadPool {
	
	public static final int ACCEPTOR = 1;
	public static final int RECEIVER = 2;
	public static final int SENDER = 3;
	
	private int max = 10;
	private int min = 2;
	private int current = 0;
	
	private final Object monitor = new Object();
	private final List<Thread> pool = new ArrayList<Thread>();
	
	private int mType;
	
	public ThreadPool(int type) {
		if ( Connector.getType().equals(Connector.TYPE_SERVER) )
			min = 2;
		else
			min = 1;
		max = 10;
		mType = type;
		init();
	}
	
	public ThreadPool(int type, int min, int max) {
		mType = type;
		this.min = min;
		this.max = max;
		init();
	}
	
	private void init() {
		for ( int i = 0; i < min; i++ ) {
			Thread t = createThread(i);
			if ( t != null )
				pool.add(t);
		}
	}
	
	private synchronized Thread createThread(int index) {
		Thread thread = null;
		switch ( mType ) {
		case ACCEPTOR :
			thread = new Acceptor(index+1);
			break;
		case RECEIVER :
			thread = new Receiver(index+1);
			break;
		case SENDER :
			thread = new Sender(index+1);
			break;
		}
		
		if ( thread == null )
			return null;
		
		current++;
		return thread;
	}

	public void addThread(int index) {
		synchronized ( monitor ) {
			if ( current < max ) {
				Thread t = createThread(index);
				t.start();
				pool.add(t);
			}
		}
	}

	public void removeThread() {
		synchronized ( monitor ) {
			if ( current > min ) {
				Thread t = (Thread) pool.remove(0);
				t.interrupt();
				t = null;
			}
		}
	}

	public void startAll() {
		synchronized ( monitor ) {
			if ( pool.isEmpty() )
				init();
			Iterator<Thread> iter = pool.iterator();
			while ( iter.hasNext() ) {
				Thread thread = (Thread) iter.next();
				thread.start();
			}
		}
	}

	public void stopAll() {
		synchronized ( monitor ) {
			Iterator<Thread> iter = pool.iterator();
			while ( iter.hasNext() ) {
				Thread thread = (Thread) iter.next();				
				thread.interrupt();
				while ( thread.isAlive() ) {}				
				Log.N(thread.getName(), "Stopped.");
				thread = null;
			}
			pool.clear();
		}
	}
}
