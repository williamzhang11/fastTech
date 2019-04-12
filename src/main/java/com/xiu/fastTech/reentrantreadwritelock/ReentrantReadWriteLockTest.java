package com.xiu.fastTech.reentrantreadwritelock;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *读写锁操作，锁降级的用处
 *
 */
public class ReentrantReadWriteLockTest {

	volatile static boolean cacheValid;
	public static void main(String[] args) {
		new ReentrantReadWriteLockTest().exec();
	}
	
	public void exec() {
		
		Cache cache = new Cache();
		for(int i=0;i<10;i++) {
			new Thread(new CacheThread(cache, String.valueOf(i))).start();
		}
		
		for(int i=0;i<10;i++) {
			new Thread(new CacheThread(cache, String.valueOf(i))).start();
		}
	}
	
	 static class CacheThread implements Runnable{
		 
		private Cache cache;
		private String key;
		
		CacheThread( Cache cache,String key) {
			this.cache = cache;
			this.key = key;
		}
		
		public void run() {
			
			System.out.println(cache.get(key));
		}
		
		
	}
	
	 static class Cache{
		
		private  Map<String, Object> cacheMap = new HashMap<String, Object>();
		private  ReadWriteLock  rw = new ReentrantReadWriteLock();
		
		
		public Object get(String key) {
			
			Object value = null;
			rw.readLock().lock();
			try {
				if((value=cacheMap.get(key))==null) {
					try {
						rw.readLock().unlock();
						rw.writeLock().lock();
						if(value == null) {
							value = key;
							cacheMap.put(key, value);
						}
						rw.readLock().lock();//锁降级
					}finally {
						rw.writeLock().unlock();
					}
					
				}
				return value;
			}finally {
				
				rw.readLock().unlock();
			}
			
		
		}
		
	}
}
