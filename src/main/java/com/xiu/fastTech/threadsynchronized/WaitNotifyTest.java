package com.xiu.fastTech.threadsynchronized;

import java.util.concurrent.TimeUnit;


/*
 * https://www.jianshu.com/p/91d95bb5a4bd
 * wait/notify通知机制
 * 循环防止假唤醒，flag防止通知丢失
 * 
 */
public class WaitNotifyTest {

	protected static Object object = new Object();
	protected static Boolean flag = false;
	public static void main(String[] args) {
		
		//生产者先启动
		/*Thread product = new Thread(new Product());
		product.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread  consumer =new Thread(new Consumer());
		consumer.start();*/
		
		
		//消费者先启动
		Thread  consumer =new Thread(new Consumer());
		consumer.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Thread product = new Thread(new Product());
		product.start();
		
		
		
		
		
	}
	
	static class Product implements Runnable{

		public void run() {
			
			synchronized (object) {
				System.out.println("进入生产者线程");
				try {
					TimeUnit.MICROSECONDS.sleep(2000);
					flag =true;
					object.notify();
					TimeUnit.MICROSECONDS.sleep(1000);
					System.out.println("退出生产者线程");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
		
	}
	
	static class Consumer implements Runnable{

		public void run() {
			synchronized (object) {
				System.out.println("进入消费者线程");
				while(!flag) {
					try {
						System.out.println("还没生产进入等待");
						object.wait();
						System.out.println("结束等待");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				System.out.println("消费");
			}
		}
		
	}
}
