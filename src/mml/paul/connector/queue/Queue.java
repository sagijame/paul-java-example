package mml.paul.connector.queue;

public interface Queue {
	
	public Job pop(int eventType) throws InterruptedException;
	public void push(Job job);
	
}
