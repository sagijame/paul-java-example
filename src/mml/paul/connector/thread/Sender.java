package mml.paul.connector.thread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import mml.paul.connector.Connection;
import mml.paul.connector.NetworkInfo;
import mml.paul.connector.pool.ByteBufferPool;
import mml.paul.connector.pool.PoolManager;
import mml.paul.connector.queue.DataEventQueue;
import mml.paul.connector.queue.Job;
import mml.paul.connector.queue.Log;

public class Sender extends Thread {

	private String mName = "Sender";	
	
	public Sender(int index) {
		setName(mName + index);
	}
	
	@Override
	public void start() {
		Log.N(getName(), "Started.");
		super.start();
	}
	
	@Override
	public void run() {
		SocketChannel sc = null;
		try {
			while ( !Thread.currentThread().isInterrupted() ) {			
				Job job = DataEventQueue.getInstance().pop(Job.EVENT_SEND);
				Object obj = job.get(Job.KEY_SEND_DATA);				
				sc = (SocketChannel)job.get(Job.KEY_CHANNEL);
				
				if ( obj instanceof String )
					write(sc, (String)obj);
			}
		} catch ( InterruptedException e ) {
			if ( Thread.currentThread().isInterrupted() )
				Log.W(getName(), "run()", "Interrupted.");
			else
				Log.E(getName(), "run()", e.toString());
		} catch (IOException e) {
			if ( sc != null )
				closeChannel(sc);
		}
	}
	
	private void write(SocketChannel sc, Object obj) throws IOException {						
		if ( sc != null && sc.isConnected() ) {			
			ByteBufferPool bufferPool = PoolManager.getByteBufferPool();
			ByteBuffer buffer = null;
			try {
				buffer = bufferPool.getMemoryBuffer();
				if ( obj instanceof String ) {
					String str = (String)obj;
					buffer.put(str.getBytes());
					buffer.flip();
					//데이터 전송이 일부분만 되었을 수 있으므로 버퍼를 확인해서 모두 보낸다.
					int writeBytes = 0;
					while ( buffer.hasRemaining() ) {
						int write = sc.write(buffer);
						writeBytes = writeBytes + write;						
					}
					buffer.rewind();
					Log.S(getName(), sc.socket().getInetAddress().getHostAddress(), String.valueOf(sc.socket().getPort()), String.valueOf(writeBytes), str);
				}
			} catch (Exception e) {
				Log.E(getName(), "write", e.toString());
				e.printStackTrace();
			} finally {
				bufferPool.putBuffer(buffer);
			}
		}
	}
	
	private void closeChannel(SocketChannel sc) {		
		try {					
			sc.close();
			String address = Connection.getInstance().remove(sc);
			if ( address != null )
				Log.W(getName(), "closeChannel()", "Disconnected from" + address + ".");									
		} catch ( IOException e ) {
			Log.E(getName(), "closeChannel()", e.toString());
		}
	}
		
	private static void pushMyJob(SocketChannel sc, Object data) {
		Job job = new Job(Job.EVENT_SEND);
		job.put(Job.KEY_CHANNEL, sc);
		job.put(Job.KEY_SEND_DATA, data);
		
		DataEventQueue.getInstance().push(job);
	}
	
	public static void send(SocketChannel to, Object obj) {		
		pushMyJob(to, obj);
	}
	
	public static void broadcast(SocketChannel to, Object obj) {
		Iterator<NetworkInfo> conns = Connection.getInstance().iterator();		
		while ( conns.hasNext() ) {			
			NetworkInfo ni = conns.next();			
			pushMyJob(ni.getSocketChannel(), obj);
		}
	}

}
