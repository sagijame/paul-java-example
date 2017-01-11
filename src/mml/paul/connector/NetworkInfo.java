package mml.paul.connector;

import java.nio.channels.SocketChannel;

public class NetworkInfo {
	
	private String ip;
	private int port;
	private SocketChannel sc;
	
	public NetworkInfo(String ip, int port, SocketChannel sc) {
		this.ip = ip;
		this.port = port;
		this.sc = sc;
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public SocketChannel getSocketChannel() {
		return sc;
	}
	
	public String getAddress() {
		return ip + ":" + port;
	}
	
}
