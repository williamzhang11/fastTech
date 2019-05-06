package com.xiu.fastTech.linkedblockingqueue;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueTest {

	public static void main(String[] args) throws InterruptedException {
		
		LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<String>();
		linkedBlockingQueue.put("1");
		linkedBlockingQueue.put("2");
		linkedBlockingQueue.remove();
		
		System.out.println(linkedBlockingQueue.toString());
		
		System.out.println(Integer.MAX_VALUE);
		
	}
	
}
