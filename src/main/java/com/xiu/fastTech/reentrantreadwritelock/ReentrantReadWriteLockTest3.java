package com.xiu.fastTech.reentrantreadwritelock;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 *多线程获取读写锁测试
 *多个线程可以同时获取到读锁
 *多个线程同时获取写锁时，是互斥的
 *锁降级，目的是将当前线程的写锁将为读锁，其他只需要读锁的线程可以获取读锁，达到并发读的目的
 *https://ask.csdn.net/questions/678480
 */
public class ReentrantReadWriteLockTest3 {
	
	
	public static void main(String[] args) throws InterruptedException {
		
		new ReentrantReadWriteLockTest3().exec();
	}
	
	public void exec() throws InterruptedException {
		
		TestOperator testOperator = new TestOperator();
		new Thread(new ReentrantReadWriteThread(testOperator)).start();
		
		//Thread.sleep(100);
		
		new Thread(new ReentrantReadWriteThread(testOperator)).start();
	}

	static class ReentrantReadWriteThread implements Runnable{
		
		TestOperator testOperator;
		
		public ReentrantReadWriteThread(TestOperator testOperator) {
			this.testOperator = testOperator;
		}
		
		public void run() {
				try {
					testOperator.fun3();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
	
	static class TestOperator{
		
		private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private java.util.Map<String, Object> map = new HashMap<String,Object>();
		
		
		/**
		 * 多个线程可以同时获取到读锁
		 * @throws InterruptedException
		 */
		public void fun1() throws InterruptedException {
			
			readWriteLock.readLock().lock();
			
			try {
				
				System.out.println("threadName:"+Thread.currentThread().getName()+"获取到读锁"+"fun1");
				Thread.sleep(1000);
				
			}finally {
				readWriteLock.readLock().unlock();
				System.out.println("threadName:"+Thread.currentThread().getName()+"释放读锁"+"fun1");
			}
		}
		/**
		 * 多线程获取写锁是互斥的
		 * @throws InterruptedException
		 */
		public void fun2() throws InterruptedException {
			
			readWriteLock.writeLock().lock();
			
			try {
				
				System.out.println("threadName:"+Thread.currentThread().getName()+"获取到写锁"+"fun2");
				Thread.sleep(1000);
				
			}finally {
				readWriteLock.writeLock().unlock();
				System.out.println("threadName:"+Thread.currentThread().getName()+"释放写锁"+"fun2");
			}
			
		}
		/**
		 * 锁降级，
		 * @throws InterruptedException 
		 */
		public Object fun3() throws InterruptedException {
			
			readWriteLock.readLock().lock();
			System.out.println("threadName:"+Thread.currentThread().getName()+"获取读锁"+"fun3");
			
			try {
				if(map.get("1")==null) {
					readWriteLock.readLock().unlock();
					System.out.println("threadName:"+Thread.currentThread().getName()+"释放读锁"+"fun3");
					readWriteLock.writeLock().lock();
					try {
							System.out.println("threadName:"+Thread.currentThread().getName()+"获取写锁"+"fun3");
							
							readWriteLock.readLock().lock();
							System.out.println("threadName:"+Thread.currentThread().getName()+"获取读锁"+"fun3");
						
					}finally {
						System.out.println("threadName:"+Thread.currentThread().getName()+"释放写锁"+"fun3");
						readWriteLock.writeLock().unlock();
					}
					
					if(map.get("1")==null) {
						System.out.println("threadName:"+Thread.currentThread().getName()+".............执行中.............."+"fun3");
						//Thread.sleep(1000);
						map.put("1","100");
						
					}
				}
				
				return map.get("1");
				
			}finally {
				System.out.println("threadName:"+Thread.currentThread().getName()+"释放读锁"+"fun3");
				readWriteLock.readLock().unlock();
			}
			
		}
		
		public void fun4() {
			
			readWriteLock.writeLock().lock();
			System.out.println("threadName:"+Thread.currentThread().getName()+"获取写锁"+"fun5");
			try {
				if(map.get("1")==null) {
					
					map.put("1","100");
				}
				
			}finally {
				
				readWriteLock.writeLock().unlock();
				System.out.println("threadName:"+Thread.currentThread().getName()+"释放写锁"+"fun5");
			}
		}
		
		public void fun5() {
			
			readWriteLock.readLock().lock();
			
			try {
				System.out.println("threadName:"+Thread.currentThread().getName()+"获取读锁"+"fun5 value:"+ map.get("1"));
				
			}finally {
				
				readWriteLock.readLock().unlock();
				System.out.println("threadName:"+Thread.currentThread().getName()+"释放读锁"+"fun5");
			}
		}
		
		public void fun6() {
			
			readWriteLock.writeLock().lock();
			System.out.println("threadName:"+Thread.currentThread().getName()+"获取写锁"+"fun6");
			if(map.get("1")==null) {
				System.out.println("threadName:"+Thread.currentThread().getName()+"写入"+"fun6");
				map.put("1", "22");
			}
			readWriteLock.writeLock().unlock();
			System.out.println("threadName:"+Thread.currentThread().getName()+"释放写锁"+"fun6");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			readWriteLock.readLock().lock();
			
			try {
				
				System.out.println("threadName:"+Thread.currentThread().getName()+"获取读锁"+"fun6 value:"+ map.get("1"));
				
			}finally {
				
				readWriteLock.readLock().unlock();
				System.out.println("threadName:"+Thread.currentThread().getName()+"释放读锁"+"fun6");
			}
		}
	}
}
