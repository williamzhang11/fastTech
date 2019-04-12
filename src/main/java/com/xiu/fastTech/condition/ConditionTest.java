package com.xiu.fastTech.condition;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *生产者消费者模式 
 *使用while（条件）{condition.await}防止假唤醒
 */
public class ConditionTest {

	public static void main(String[] args) throws InterruptedException {
		new ConditionTest().exec();
	}
	
	public void exec() throws InterruptedException {
		
		ConditionOperator conditionOperator = new ConditionOperator(1);
		new Thread(new ConsumerThread(conditionOperator)).start();
		new Thread(new ProductThread(conditionOperator)).start();
		
	}
	
	static class ProductThread implements Runnable{
		
		ConditionOperator conditionOperator;
		public ProductThread(ConditionOperator conditionOperator) {
			this.conditionOperator = conditionOperator;
		}
		public void run() {
			Thread.currentThread().setName("product");
			for(int i=0;i<11;i++)
				try {
					System.out.println("product:"+i);
					conditionOperator.set(String.valueOf(i));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			System.out.println(Thread.currentThread().getName()+"end============");
		}
		
	}
	
	static class ConsumerThread implements Runnable{
		
		ConditionOperator conditionOperator;
		public ConsumerThread(ConditionOperator conditionOperator) {
			this.conditionOperator = conditionOperator;
		}
		public void run() {
			Thread.currentThread().setName("Consumer");
			try {
				String value;
				while (( value = conditionOperator.get())!=null) {
					System.out.println("consumer:"+value);
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName()+"end===============");
		}
		
	}
	
	
	
	
	static class ConditionOperator{
		
		private LinkedList<String>buffer;
		
		private int maxSize;
		
		private Lock lock;
		
		private Condition emptyCondition;
		
		private Condition fullCondition;
		
		public ConditionOperator(int maxSize) {
			this.maxSize = maxSize;
			buffer = new LinkedList<String>();
			lock = new ReentrantLock();
			emptyCondition = lock.newCondition();
			fullCondition = lock.newCondition();
		}
		
		public void set(String value) throws InterruptedException {
			
			lock.lock();
			
			try {
				
				while(maxSize ==buffer.size()) {
					fullCondition.await();
				}
				
				buffer.add(value);
				emptyCondition.signal();
				
			}finally {
				lock.unlock();
			}
			
		}
		
		public String get() throws InterruptedException {
			
			String value;
			lock.lock();
			
			try {
				while (buffer.size()==0) {
					emptyCondition.await();
				}
				
				value = buffer.poll();
				fullCondition.signal();
				
			}finally {
				lock.unlock();
			}
			return value;
		}
	}
	
	
}
