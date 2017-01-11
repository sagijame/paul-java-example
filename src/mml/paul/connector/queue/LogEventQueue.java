package mml.paul.connector.queue;

import java.util.ArrayList;
import java.util.List;

public class LogEventQueue implements Queue {
	
	private int max = 100;
	
	private final Object mMonitor = new Object();	
	private final List<Job> mQueue = new ArrayList<Job>();
	
	private static LogEventQueue instance = new LogEventQueue();
	
	public static LogEventQueue getInstance() {
		if ( instance == null ) {
			synchronized ( LogEventQueue.class ) {
				instance = new LogEventQueue();
			}
		}
		return instance;
	}
	
	@Override
	public void push(Job job) {
		if ( job != null ) {
			int eventType = job.getEventType();			
			switch ( eventType ) {
			case Log.EVENT_LOG :
				synchronized ( mMonitor ) {
					if ( mQueue.size() > max ) {
						mQueue.remove(mQueue.size()-1);
					}
					mQueue.add(job);
					mMonitor.notify();
				}
				break;
			default :
				throw new IllegalArgumentException("Illegal EventType...");
			}
		}
	}

	@Override
	public Job pop(int eventType) {
		switch ( eventType ) {
		case Log.EVENT_LOG :
			synchronized ( mMonitor ) {
				if ( mQueue.isEmpty() ) {
					try {
						mMonitor.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return (Job) mQueue.remove(0);
			}
		default :
			throw new IllegalArgumentException("Illegal EventType...");
		}
	}
	
	public void clear() {
		synchronized ( mMonitor ) {
			mQueue.clear();
		}
	}
	
}
