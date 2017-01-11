package mml.paul.connector.queue;

import java.util.ArrayList;
import java.util.List;

public class DataEventQueue implements Queue {
	
	private final Object mReceiveDataMonitor = new Object();
	private final Object mSendDataMonitor = new Object();
		
	private final List<Job> mReceiveDataQueue = new ArrayList<Job>();
	private final List<Job> mSendDataQueue = new ArrayList<Job>();
	
	private static DataEventQueue instance = new DataEventQueue();
	public static DataEventQueue getInstance() {
		if ( instance == null ) {
			synchronized ( DataEventQueue.class ) {
				instance = new DataEventQueue();
			}
		}
		return instance;
	}
	
	@Override
	public Job pop(int eventType) throws InterruptedException {
		switch ( eventType ) {
		case Job.EVENT_RECEIVE :
			return getReceiveData();
		case Job.EVENT_SEND :
			return getSendData();
		default :
			throw new IllegalArgumentException("Illegal EventType...");
		}
	}
	
	@Override
	public void push(Job job) {
		if ( job != null ) {
			int eventType = job.getEventType();			
			switch ( eventType ) {			
			case Job.EVENT_RECEIVE :
				putReceiveData(job);
				break;
			case Job.EVENT_SEND :
				putSendData(job);
				break;
			default :
				throw new IllegalArgumentException("Illegal EventType...");
			}
		}		
	}
	
	private Job getReceiveData() throws InterruptedException {		
		synchronized ( mReceiveDataMonitor ) {
			if ( mReceiveDataQueue.isEmpty() ) 
				mReceiveDataMonitor.wait();
			return (Job) mReceiveDataQueue.remove(0);
		}		
	}
	
	private Job getSendData() throws InterruptedException {		
		synchronized ( mSendDataMonitor ) {
			if ( mSendDataQueue.isEmpty() )
				mSendDataMonitor.wait();
			return (Job) mSendDataQueue.remove(0);
		}		
	}
		
	private void putReceiveData(Job job) {
		synchronized ( mReceiveDataMonitor ) {
			mReceiveDataQueue.add(job);
			mReceiveDataMonitor.notify();
		}
	}
	
	private void putSendData(Job job) {
		synchronized ( mSendDataMonitor ) {
			mSendDataQueue.add(job);
			mSendDataMonitor.notify();
		}
	}

}
