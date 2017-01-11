package mml.paul.connector.queue;

import java.util.ArrayList;
import java.util.List;

public class JobEventQueue implements Queue {
	
	private final Object mAcceptMonitor = new Object();
	private final Object mReceiveMonitor = new Object();
	
	private final List<Job> mAcceptQueue = new ArrayList<Job>();
	private final List<Job> mReceiveQueue = new ArrayList<Job>();
	
	private static JobEventQueue instance = new JobEventQueue();
	
	public static JobEventQueue getInstance() {
		if ( instance == null ) {
			synchronized ( JobEventQueue.class ) {
				instance = new JobEventQueue();
			}
		}
		return instance;
	}
	
	private JobEventQueue() {
		
	}
	
	@Override
	public void push(Job job) {
		if ( job != null ) {
			int eventType = job.getEventType();			
			switch ( eventType ) {
			case Job.EVENT_ACCEPT :
				putAcceptJob(job);
				break;
			case Job.EVENT_RECEIVE :
				putReceiveJob(job);
				break;
			default :
				throw new IllegalArgumentException("Illegal EventType...");
			}
		}
	}

	@Override
	public Job pop(int eventType) throws InterruptedException, IndexOutOfBoundsException {
		switch ( eventType ) {
		case Job.EVENT_ACCEPT :
			return getAcceptJob();
		case Job.EVENT_RECEIVE :
			return getReceiveJob();
		default :
			throw new IllegalArgumentException("Illegal EventType...");
		}
	}
	
	private Job getAcceptJob() throws InterruptedException {
		synchronized ( mAcceptMonitor ) {
			if ( mAcceptQueue.isEmpty() ) {
				mAcceptMonitor.wait();				
			}
			return (Job) mAcceptQueue.remove(0);
		}
	}
	
	private Job getReceiveJob() throws InterruptedException {
		synchronized ( mReceiveMonitor ) {
			if ( mReceiveQueue.isEmpty() ) 
				mReceiveMonitor.wait();
			return (Job) mReceiveQueue.remove(0);
		}		
	}
	
	private void putAcceptJob(Job job) {
		synchronized ( mAcceptMonitor ) {
			mAcceptQueue.add(job);
			mAcceptMonitor.notify();
		}
	}
	
	private void putReceiveJob(Job job) {
		synchronized ( mReceiveMonitor ) {
			mReceiveQueue.add(job);
			mReceiveMonitor.notify();
		}
	}
	
}
