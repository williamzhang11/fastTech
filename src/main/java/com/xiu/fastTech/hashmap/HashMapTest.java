package com.xiu.fastTech.hashmap;

import java.util.HashMap;
import java.util.Map;


public class HashMapTest {

	public static void main(String[] args) {
		Map<String,Object> map = new HashMap<String, Object>(100);
		map.put(null, "111");
		System.out.println(map.get(null));
	}
}
