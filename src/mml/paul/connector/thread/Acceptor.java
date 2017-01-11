package mml.paul.connector.thread;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mml.paul.connector.pool.PoolManager;
import mml.paul.connector.queue.Job;
import mml.paul.connector.queue.JobEventQueue;
import mml.paul.connector.queue.Log;

public class Acceptor extends Thread {
	
	public Acceptor(int index) {
		setName("Acceptor" + index);
	}
	
	@Override
	public void start() {
		Log.N(getName(), "Started.");
		super.start();
	}
	
	@Override
	public void run() {		
		try {
			while ( !Thread.currentThread().isInterrupted() ) {
				try {
					Job job = JobEventQueue.getInstance().pop(Job.EVENT_ACCEPT);
					SocketChannel sc = (SocketChannel)job.get(Job.KEY_CHANNEL);
					sc.configureBlocking(false);	
					
					ReceiveSelector receiver = (ReceiveSelector)PoolManager.getReceiveSelectorPool().get();
					receiver.registConnection(sc);
				} catch ( IOException e ) {				 
					Log.W(getName(), "run()", e.toString());
				}
			}	
		} catch ( InterruptedException e ) {
			if ( Thread.currentThread().isInterrupted() )
				Log.W(getName(), "run()", "Interrupted.");
			else
				Log.E(getName(), "run()", e.toString());
		}
	}
	
}
