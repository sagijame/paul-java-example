package mml.paul.connector.queue;

@SuppressWarnings("serial")
public class Log extends Job {
	
	public static final int EVENT_LOG = 4;
	
	public static final String TYPE_NOTIFICATION = "N";
	public static final String TYPE_WARNING = "W";
	public static final String TYPE_ERROR = "E";
	public static final String TYPE_RECEIVE = "R";
	public static final String TYPE_SEND = "S";
	
	public static final String KEY_LOG_MSG = "log_msg";
	public static final String KEY_LOG_TIME = "log_time";
	public static final String KEY_LOG_TYPE = "log_type";
	public static final String KEY_LOG_TAG = "log_tag";
	public static final String KEY_LOG_IP = "log_ip";
	public static final String KEY_LOG_PORT = "log_port";
	public static final String KEY_LOG_FUNCTION = "log_function";
	public static final String KEY_LOG_BYTES = "log_bytes";
		
	public static void N(String tag, String msg) {
		Log l = new Log();		
		l.put(KEY_LOG_TYPE, TYPE_NOTIFICATION);
		push(l, tag, msg);
	}
	
	public static void W(String tag, String function, String msg) {
		Log l = new Log();		
		l.put(KEY_LOG_TYPE, TYPE_WARNING);
		l.put(KEY_LOG_FUNCTION, function);
		push(l, tag, msg);
	}
	
	public static void E(String tag, String function, String msg) {
		Log l = new Log();		
		l.put(KEY_LOG_TYPE, TYPE_ERROR);
		l.put(KEY_LOG_FUNCTION, function);
		push(l, tag, msg);
	}
	
	public static void R(String tag, String ip, String port, String bytes, String msg) {
		Log l = new Log();		
		l.put(KEY_LOG_TYPE, TYPE_RECEIVE);
		l.put(KEY_LOG_IP, ip);
		l.put(KEY_LOG_PORT, port);
		l.put(KEY_LOG_BYTES, bytes);
		push(l, tag, msg);
	}
	
	public static void S(String tag, String ip, String port, String bytes, String msg) {
		Log l = new Log();		
		l.put(KEY_LOG_TYPE, TYPE_SEND);
		l.put(KEY_LOG_IP, ip);
		l.put(KEY_LOG_PORT, port);
		l.put(KEY_LOG_BYTES, bytes);
		push(l, tag, msg);
	}
	
	private static void push(Log l, String tag, String msg) {
		l.put(KEY_EVENT_TYPE, EVENT_LOG);
		l.put(KEY_LOG_TAG, tag);
		l.put(KEY_LOG_MSG, msg);
		l.put(KEY_LOG_TIME, System.currentTimeMillis());		
		LogEventQueue.getInstance().push(l);
	}
	
}
