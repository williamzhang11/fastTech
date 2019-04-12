package com.xiu.fastTech.condition;
/**
 *3个线程依次顺序打印ABC 
 *
 */

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class ABCBusiness {
	
	public static void main(String[] args) {
		new ABCBusiness().exec();
	}
	
	public void exec() {
		Business business = new Business();
		new CThread(business).start();
		new AThread(business).start();
		new BThread(business).start();
		
		
	}
	
	
	static class AThread extends Thread{
		
		Business business;
		public AThread(Business business) {
			this.business = business;
		}
		
		public void run() {
			for(int i=0;i<10;i++) {
				try {
					business.printA();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	static class BThread extends Thread{
		
		Business business;
		public BThread(Business business) {
			this.business = business;
		}
		public void run() {
			for(int i=0;i<10;i++) {
				try {
					business.printB();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	static class CThread extends Thread{
		
		Business business;
		public CThread(Business business) {
			this.business = business;
		}
		public void run() {
			for(int i=0;i<10;i++) {
				try {
					business.printC();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	static class Business{
		
		ReentrantLock reentrantLock = new ReentrantLock();
		Condition aCondition = reentrantLock.newCondition();
		Condition bCondition = reentrantLock.newCondition();
		Condition cCondition = reentrantLock.newCondition();
		private String value = "A";
		
		public void printA() throws InterruptedException {
			reentrantLock.lock();
			try {
				
				while(!"A".equals(value)) {
					aCondition.await();
				}
				
				System.out.println("A");
				value="B";
				bCondition.signal();
				
				
			}finally {
				reentrantLock.unlock();
			}
		}
		
		public void printB() throws InterruptedException {
			reentrantLock.lock();
			try {
				while (!"B".equals(value) ) {
					bCondition.await();
				}
				
				System.out.println("B");
				value="C";
				cCondition.signal();
			}finally {
				reentrantLock.unlock();
			}
		}
		
		public void printC() throws InterruptedException {
			
			reentrantLock.lock();
			
			try {
			while (!"C".equals(value)) {
				cCondition.await();
			}
			
			System.out.println("C");
			value = "A";
			aCondition.signal();
			}finally {
				reentrantLock.unlock();
			}
		}
	}
	
}
