package com.xiu.fastTech.linkedlist;

import java.util.Deque;
import java.util.LinkedList;
//LinkedList实现堆栈
public class LinkedListDequeTest {

	public static void main(String[] args) {
		Deque<Integer>queue = new LinkedList<Integer>();
		Integer i = 10;
		while( i>0) {
			queue.addLast(i);
			i --;
		}
		Integer i1 = null;
		while(( i1= queue.removeLast()) != null) {
			System.out.println(i1);
			
		}
		
	}
}
