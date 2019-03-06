package com.xiu.fastTech.linkedhashmap;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//默认插入顺序,可通过其他重载构造函数，修改为访问顺序
public class LinkedHashMapTest {

	public static void main(String[] args) {
		//插入顺序
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		map.put("1", 1);
		map.put("2", 2);
		map.put("3", 3);
		map.put("4", 4);
		map.put("5", 5);
		
		Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
		Iterator<Entry<String, Integer>> it = entrySet.iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> map1 = (Entry<String, Integer>)it.next();
			
			System.out.println("key:"+map1.getKey()+"==>value:"+map1.getValue());;
		}
		System.out.println("=========================================================");
		//访问顺序
		Map<String, Integer> map1 = new LinkedHashMap<String, Integer>(10,0.75f,true);
		map1.put("6", 6);
		map1.put("7", 7);
		map1.put("8", 8);
		map1.put("9", 9);
		map1.put("10", 10);
		
		map1.get("8");
		map1.get("6");
		
		Set<Map.Entry<String, Integer>> entrySet1 = map1.entrySet();
		Iterator<Entry<String, Integer>> it1 = entrySet1.iterator();
		while (it1.hasNext()) {
			Map.Entry<String, Integer> map2 = (Entry<String, Integer>)it1.next();
			
			System.out.println("key:"+map2.getKey()+"==>value:"+map2.getValue());;
		}
		
		
	}
}
