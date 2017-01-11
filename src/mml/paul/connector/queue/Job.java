package mml.paul.connector.queue;

import java.util.HashMap;

@SuppressWarnings("serial")
public class Job extends HashMap<String, Object> {
	
	public static final String KEY_EVENT_TYPE = "event_type";
	public static final String KEY_CHANNEL = "channel";
	public static final String KEY_SEND_DATA = "send_data";
	public static final String KEY_RECEIVE_DATA = "receive_data";
		
	public static final int EVENT_ACCEPT = 1;
	public static final int EVENT_RECEIVE = 2;
	public static final int EVENT_SEND = 3;
	
	public Job() {		
	}
	
	public Job(int eventType) {
		put(KEY_EVENT_TYPE, eventType);
	}
	
	public int getEventType() {
		return (Integer)get(KEY_EVENT_TYPE);
	}
	
	public void setEventType(int eventType) {
		put(KEY_EVENT_TYPE, eventType);
	}
	
}
