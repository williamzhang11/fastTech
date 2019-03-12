package com.xiu.fastTech.treemap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
public class TreeMapTest {
	
	public static void main(String[] args) {
		
		TreeMap<Integer, Integer> treeMap = new TreeMap<Integer, Integer>();
		treeMap.put(1, 1);
		treeMap.put(2, 2);
		treeMap.put(3, 3);
		treeMap.put(4, 4);
		treeMap.put(5, 5);
		
		Iterator<Entry<Integer, Integer>> setIterator = treeMap.entrySet().iterator();
		
		while (setIterator.hasNext()) {
			Entry<Integer, Integer> entry = setIterator.next();
			System.out.println("key"+entry.getKey()+"==>value:"+entry.getValue());
			
		}
		
		System.out.println("============内部比较器Comparable==================");
		TreeMap<UserComparable, Integer> userTreeMap = new TreeMap<UserComparable, Integer>();
		UserComparable user2 = new UserComparable(20, "lisi");
		UserComparable user1 = new UserComparable(10, "zhangsan");
		UserComparable user3 = new UserComparable(20, "wangwu");
		
		userTreeMap.put(user1, 1);
		userTreeMap.put(user2, 2);
		userTreeMap.put(user3, 3);
		
		System.out.println(userTreeMap);
		
		System.out.println("============外部比较器Comparator==================");
		
		TreeMap<User, Integer> userComparatorTreeMap= new TreeMap<User, Integer>(new Comparator<User>() {
			public int compare(User o1, User o2) {
				return o1.getAge()-o2.getAge();
			}
		});
		
		User user4 = new User(20, "lisi");
		User user5 = new User(10, "zhangsan");
		User user6 = new User(20, "wangwu");
		
		userComparatorTreeMap.put(user4, 1);
		userComparatorTreeMap.put(user5, 2);
		userComparatorTreeMap.put(user6, 3);
		
		System.out.println(userComparatorTreeMap);
		
		System.out.println("============HashMap==================");
		
		Map<UserComparable, Integer> map = new HashMap<UserComparable, Integer>();
		map.put(user1, 1);
		map.put(user1, 2);
		map.put(user2, 3);
		System.out.println(map);
		
		System.out.println("============treeMap==================");
		
		Map<UserComparable, Integer> treeMap1 = new TreeMap<UserComparable, Integer>();
		treeMap1.put(user1, 1);
		treeMap1.put(user1, 2);
		treeMap1.put(user2, 3);
		System.out.println(treeMap1);
		
		
	}
}
