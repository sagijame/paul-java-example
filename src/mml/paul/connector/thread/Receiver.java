package mml.paul.connector.thread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnmappableCharacterException;

import mml.paul.connector.Connection;
import mml.paul.connector.pool.ByteBufferPool;
import mml.paul.connector.pool.PoolManager;
import mml.paul.connector.queue.DataEventQueue;
import mml.paul.connector.queue.Job;
import mml.paul.connector.queue.JobEventQueue;
import mml.paul.connector.queue.Log;

public class Receiver extends Thread {
	
	private Charset mCharset = null;
	private CharsetDecoder mDecoder = null;
	
	public Receiver(int index) {
		setName("Rreceiver" + index);
		
		mCharset = Charset.forName("UTF-8");
		mDecoder = mCharset.newDecoder();		
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
					Job job = JobEventQueue.getInstance().pop(Job.EVENT_RECEIVE);
					sc = (SocketChannel)job.get(Job.KEY_CHANNEL);			
					read(sc);
			}
		} catch (IndexOutOfBoundsException e2) {
			Log.W(getName(), "run()", e2.toString());	
		} catch (IOException e1) {
			if ( e1 instanceof UnmappableCharacterException ) {
				Log.E(getName(), "run()", e1.toString());
			} else
				if ( sc != null )
					closeChannel(sc);
		} catch ( InterruptedException e ) {	
			if ( Thread.currentThread().isInterrupted() )
				Log.W(getName(), "run()", "Interrupted");
			else
				Log.E(getName(), "run()", e.toString());
		}
	}
		
	private void read(SocketChannel sc) throws IOException {
		ByteBufferPool bufferPool = PoolManager.getByteBufferPool();
		ByteBuffer buffer = null;
		try {
			buffer = bufferPool.getMemoryBuffer();
			// 데이터의 일부만 전송되었을 수 있으므로 두 번 read한다.
			int sizeOfBytes = 0;
			for ( int i = 0; i < 2; i++ ) {
				int read = sc.read(buffer);				
				if ( read == -1 ) {
					closeChannel(sc);
					return;
				}					
				sizeOfBytes = sizeOfBytes + read;
			}
			buffer.flip();
			
			if ( sizeOfBytes == 0 )
				return;
					
			String data = mDecoder.decode(buffer).toString();
			pushMyJob(sc, data);
			
			Log.R(getName(), sc.socket().getInetAddress().getHostAddress(), String.valueOf(sc.socket().getPort()), String.valueOf(sizeOfBytes), data);		
		} finally {
			bufferPool.putBuffer(buffer);
		}
	}
	
	private static void pushMyJob(SocketChannel sc, Object data) {
		Job job = new Job(Job.EVENT_RECEIVE);
		job.put(Job.KEY_CHANNEL, sc);
		job.put(Job.KEY_RECEIVE_DATA, data);
		
		DataEventQueue.getInstance().push(job);
	}
	
	private void closeChannel(SocketChannel sc) {
		try {	
			sc.close();			
			String address = Connection.getInstance().remove(sc);
			if ( address != null )
				Log.W(getName(), "closeChannel()", "Disconnected from " + address + ".");
		} catch ( IOException e ) {
			Log.E(getName(), "closeChannel()", e.getMessage());
		}
	}

}
