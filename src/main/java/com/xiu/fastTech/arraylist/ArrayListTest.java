package com.xiu.fastTech.arraylist;

import java.util.ArrayList;
import java.util.List;

public class ArrayListTest {

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		
		list.remove(2);
		
		System.out.println(list.get(2));
		System.out.println(list.size());
	}
}
