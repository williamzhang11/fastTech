package com.xiu.fastTech.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest2 {

	public static void main(String[] args) {
		new OperatorThread(new Operator()).start();
	}
	
	
	static class OperatorThread extends Thread{
		
		Operator operator;
		public OperatorThread(Operator operator) {
			this.operator = operator;
		}
		
		
		public void run() {
		
			for(int i=0;i<10;i++) {
				
				operator.fun1();
			}
		}
	}
	
	
	static class Operator{
		
		ReentrantLock reentrantLock = new ReentrantLock();
		
		public void fun1() {
			reentrantLock.lock();
			
			try {
				System.out.println("A");
				
			}finally {
				reentrantLock.unlock();
			}
		}
	}
	
	
	
}
