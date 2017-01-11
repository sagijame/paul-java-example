package mml.paul.connector.pool;

import java.util.HashMap;
import java.util.Map;


public class PoolManager {
	
	public static final String POOL_ACCEPT_SELECTOR = "pool_accept_selector";
	public static final String POOL_RECEIVE_SELECTOR = "pool_receive_selector";
	
	public static final String POOL_BYTE_BUFFER = "pool_byte_buffer";
	
	private static Map<String, Object> map = new HashMap<String, Object>();
	
	@SuppressWarnings("unused")
	private PoolManager instance = new PoolManager();
	
	public static void registAcceptSelectorPool(SelectorPool acceptSelectorPool) {
		map.put(POOL_ACCEPT_SELECTOR, acceptSelectorPool);
	}
	
	public static void registReceiveSelectorPool(SelectorPool receiveSelectorPool) {
		map.put(POOL_RECEIVE_SELECTOR, receiveSelectorPool);
	}
	
	public static SelectorPool getAcceptSelectorPool() {
		return (SelectorPool) map.get(POOL_ACCEPT_SELECTOR);
	}
	
	public static SelectorPool getReceiveSelectorPool() {
		return (SelectorPool) map.get(POOL_RECEIVE_SELECTOR);
	}
	
	public static void registByteBufferPool(ByteBufferPool byteBufferPool) {
		map.put(POOL_BYTE_BUFFER, byteBufferPool);
	}
	
	public static ByteBufferPool getByteBufferPool() {
		return (ByteBufferPool) map.get(POOL_BYTE_BUFFER);
	}
	
}
