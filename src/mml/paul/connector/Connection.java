package mml.paul.connector;

import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Vector;

public class Connection {
	
	private static Connection instance = new Connection();
	public static Connection getInstance() {
		return instance;
	}
	
	private static Vector<NetworkInfo> connection = new Vector<NetworkInfo>();
		
	public void add(SocketChannel sc) {
		synchronized ( connection ) {
			if ( sc == null || sc.socket() == null )
				return;
			String ip = sc.socket().getInetAddress().getHostAddress();
			int port = sc.socket().getPort();
			NetworkInfo info = new NetworkInfo(ip, port, sc);
			connection.add(info);
		}		
	}
	
	public String remove(SocketChannel sc) {
		synchronized ( connection ) {
			Iterator<NetworkInfo> conn = connection.iterator();
			int pos = connection.size();
			while (--pos >= 0) {
				NetworkInfo ni = (NetworkInfo) conn.next();				
				if ( sc.equals(ni.getSocketChannel()) ) {
					String info = ni.getAddress(); 
					conn.remove();
					return info;
				}		
			}
			return null;
		}
	}
	
	public Iterator<NetworkInfo> iterator() {
		return connection.iterator();
	}
	
}
