package com.xiu.fastTech.DelayQueue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


/**
 *延迟工作 
 *
 *
 */
public class DelayQueueTest {

	public static void main(String[] args) {
		
		TaskQueueThread taskQueueThread = TaskQueueThread.getInstance();
		taskQueueThread.init();
		
		taskQueueThread.put(2, new Runnable() {
			
			public void run() {
				System.out.println("task2");
			}
		});
		
		taskQueueThread.put(1, new Runnable() {
			
			public void run() {
				System.out.println("task1");
			}
		});
		
		
		
	}
	
}
