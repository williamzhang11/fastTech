package com.xiu.fastTech.linkedlist;

import java.util.LinkedList;
import java.util.Queue;
//LinkedList实现队列
public class LinkedListQueueTest {

	public static void main(String[] args) {
		Queue<Integer>queue = new LinkedList<Integer>();
		Integer i = 10;
		while( i>0) {
			queue.add(i);
			i --;
		}
		Integer i1 = null;
		while(( i1= queue.poll()) != null) {
			System.out.println(i1);
			
		}
		
	}
}
