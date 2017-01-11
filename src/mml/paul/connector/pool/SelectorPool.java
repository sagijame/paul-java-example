package mml.paul.connector.pool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mml.paul.connector.queue.Log;
import mml.paul.connector.thread.AcceptSelector;
import mml.paul.connector.thread.ReceiveSelector;

public class SelectorPool {
	
	public static final int ACCEPT_SELECTOR = 1;
	public static final int RECEIVE_SELECTOR = 2;
	
	private int size;	
	private int type;
	private int roundRobinIndex = 0;	
	private final Object monitor = new Object();
	protected final List<Thread> pool = new ArrayList<Thread>();
			
	public SelectorPool(int type, int size) {
		this.size = size;
		this.type = type;
		init();
	}
	
	private void init() {
		for ( int i = 0; i < size; i++ ) {
			Thread t = createSelector(i);
			if ( t != null )
				pool.add(t);
		}
	}
	
	protected Thread createSelector(int index) {				
		Thread handler = null;
		switch ( type ) {
		case ACCEPT_SELECTOR :
			handler = new AcceptSelector(index);
			break;
		case RECEIVE_SELECTOR :
			handler = new ReceiveSelector(index);
			break;
		}		
		
		return handler;
	}
	
	public void startAll() {
		if ( pool.isEmpty() )
			init();		
		Iterator<Thread> iter = pool.iterator();
		while ( iter.hasNext() ) {
			Thread handler = (Thread) iter.next();
			handler.start();
		}
	}
	
	public void stopAll() {
		Iterator<Thread> iter = pool.iterator();
		while ( iter.hasNext() ) {
			Thread thread = (Thread) iter.next();				
			thread.interrupt();
			while ( thread.isAlive() ) {}
			if ( thread instanceof AcceptSelector ) {
				((AcceptSelector)thread).close();
			} else if ( thread instanceof ReceiveSelector ) {
				((ReceiveSelector)thread).close();
			}
			
			Log.N(thread.getName(), "Stopped.");
			thread = null;			
		}
		pool.clear();
	}
	
	/////////////////////////////////////////////////////////////////
	
	public Thread get() {
		synchronized ( monitor ) {
			return (Thread) pool.get(roundRobinIndex++ % size);
		}
	}
	
	public Thread get(int index) {
		synchronized ( monitor ) {
			return (Thread) pool.get(index);
		}
	}
	
	public void put(Thread handler) {
		synchronized ( monitor ) {
			if (handler != null) {
				pool.add(handler);
			}
			monitor.notify();
		}
	}
	
	public int size() {
		synchronized ( monitor ) {
			return pool.size();
		}
	}
	
	public boolean isEmpty() {
		synchronized ( monitor ) {
			return pool.isEmpty();
		}
	}
	
	///////////////////////////////////////////////////////////////

}
