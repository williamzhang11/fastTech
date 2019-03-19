package com.xiu.fastTech.threadsynchronized;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class LockConditionTest {
	
	private static Boolean flag = false;
	private static Lock lock = new ReentrantLock();
	private static Condition condition = lock.newCondition();
	
	public static void main(String[] args) {
		
		new Thread(new Consumer()).start();
		
		try {
			TimeUnit.MILLISECONDS.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(new Product()).start();
	}
	
	
	static class Product extends Thread{
		
		@Override
		public void run() {
			
			System.out.println("进入生产者");
			lock.lock();
			System.out.println("生产");
			
			flag =true;
			condition.signal();
			System.out.println("通知完成");
			
			lock.unlock();
			System.out.println("退出生产者");
		}
	}
	
	static class Consumer extends Thread{
		
		@Override
		public void run() {
			
			System.out.println("进入消费者");
			lock.lock();
			while(!flag) {
				try {
					System.out.println("11");
					condition.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("消费完成");
			lock.unlock();
			System.out.println("退出消费者");
		}
	}
}
