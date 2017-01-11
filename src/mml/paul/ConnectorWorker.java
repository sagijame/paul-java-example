package mml.paul;

import mml.paul.connector.queue.DataEventQueue;
import mml.paul.connector.queue.Job;

public class ConnectorWorker extends Thread {
	
	@Override 
	public void run() {
		try {
			while ( !Thread.currentThread().isInterrupted() ) {
				Job job = DataEventQueue.getInstance().pop(Job.EVENT_RECEIVE);
				Object obj = job.get(Job.KEY_RECEIVE_DATA);
				if ( obj instanceof String ) {
					// String str = (String) obj;
					// to do
				}
			} 
		} catch ( InterruptedException e ) {
			
		}
	}

}
