package com.xiu.fastTech.reentrantreadwritelock;

import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 *读写锁可重入测试 
 *同一个线程，获取了一个写锁后，还可以继续获取当前锁对象的读锁，但是如果先是获取锁对象的读锁，再次尝试获取写锁，是获取不到的
 *同一个线程，获取到写锁后，可以再次获取当前锁对象的写锁方法
 *同一个线程，获取到读锁后，可以再次获取当前锁对象的读锁方法
 *
 */
public class ReentrantReadWriteLockTest2 {
	
	
	public static void main(String[] args) {
		
		new ReentrantReadWriteLockTest2().exec();
	}
	
	public void exec() {
		TestOperator testOperator = new TestOperator();
		new Thread(new ReentrantReadWriteThread(testOperator)).start();
	}

	static class ReentrantReadWriteThread implements Runnable{
		
		TestOperator testOperator;
		
		public ReentrantReadWriteThread(TestOperator testOperator) {
			
			this.testOperator = testOperator;
		}
		public void run() {
			testOperator.fun1();
		}
	}
	
	
	static class TestOperator{
		
		private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		
		public void fun1() {
			
			readWriteLock.readLock().lock();
			
			try {
				
				System.out.println("fun1");
				fun4();
			}finally {
				
				readWriteLock.readLock().unlock();
			}
		}
		
		public void fun2() {
			
			readWriteLock.writeLock().lock();
			try {
				
				System.out.println("fun2");
			}finally {
				
				readWriteLock.writeLock().unlock();
			}
			
		}
		
		public void fun3() {
			
			readWriteLock.writeLock().lock();
			try {
				System.out.println("fun3");
				fun2();
			}finally {
				
				readWriteLock.writeLock().unlock();
			}
			
		}
		
		public void fun4() {
			
			readWriteLock.readLock().lock();
			
			try {
				
				System.out.println("fun4");
			}finally {
				
				readWriteLock.readLock().unlock();
			}
		}
		
		
	}
}
