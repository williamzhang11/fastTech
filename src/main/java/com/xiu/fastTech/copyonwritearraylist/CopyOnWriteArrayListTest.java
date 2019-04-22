package com.xiu.fastTech.copyonwritearraylist;

import java.util.concurrent.CopyOnWriteArrayList;


public class CopyOnWriteArrayListTest {

	CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<String>();
	
	public void exec() {
		
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		
		
		System.out.println(list.get(0));
		
	}
	
	public static void main(String[] args) {
		CopyOnWriteArrayListTest cp = new CopyOnWriteArrayListTest();
		cp.exec();
		
	}
}
