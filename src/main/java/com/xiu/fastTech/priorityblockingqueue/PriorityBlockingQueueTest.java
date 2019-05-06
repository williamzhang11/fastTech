package com.xiu.fastTech.priorityblockingqueue;

import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueTest {

	public static void main(String[] args) throws InterruptedException {
		PriorityBlockingQueue<Integer> priorityBlockingQueue = new PriorityBlockingQueue<Integer>();
		int i=10;
		while(i>0) {
			priorityBlockingQueue.offer(i);
			i--;
		}
		
		System.out.println(priorityBlockingQueue.toString());
		Integer nu = null;
		while( (nu=priorityBlockingQueue.take())!=null) {
			
			System.out.println(nu);
		}
		
		
		
	}
}
