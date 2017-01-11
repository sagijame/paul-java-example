package mml.paul.window;

import mml.paul.connector.queue.Log;
import mml.paul.connector.queue.LogEventQueue;

public class Loger extends Thread {

	@Override
	public void run() {
		while ( !Thread.currentThread().isInterrupted() ) {
			Log l = (Log)LogEventQueue.getInstance().pop(Log.EVENT_LOG);
			String type = (String)l.get(Log.KEY_LOG_TYPE);
			String tag = (String)l.get(Log.KEY_LOG_TAG);			
			String msg = (String)l.get(Log.KEY_LOG_MSG);
			String time = String.valueOf(l.get(Log.KEY_LOG_TIME));
			if ( type.equals(Log.TYPE_NOTIFICATION) ) {				
				MainFrame.log(time + " " + type + " " + tag, msg);
			} else if ( type.equals(Log.TYPE_ERROR) || type.equals(Log.TYPE_WARNING) ) {
				String function = (String)l.get(Log.KEY_LOG_FUNCTION);				
				MainFrame.log(time + " " + type + " " + tag + " " + function, msg);
			} else if ( type.equals(Log.TYPE_RECEIVE) || type.equals(Log.TYPE_SEND) ) {
				String ip = (String)l.get(Log.KEY_LOG_IP);
				String port = (String)l.get(Log.KEY_LOG_PORT);
				if ( type.equals(Log.TYPE_RECEIVE) ) {
					MainFrame.log(time + " " + type + " " + tag, msg + " from " + ip + " : " + port);
				} else if ( type.equals(Log.TYPE_SEND) ) {				
					MainFrame.log(time + " " + type + " " + tag, msg + " to " + ip + " : " + port);
				}				
			}		
		}
	}
	
}
