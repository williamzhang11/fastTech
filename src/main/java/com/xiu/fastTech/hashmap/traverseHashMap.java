package com.xiu.fastTech.hashmap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//遍历的四种方式
public class traverseHashMap {
	//静态变量与静态代码块的执行顺序，是先声明，先执行
	private static Map<String,Object> map =null;
	
	static {
		map = new HashMap<String, Object>();
		Integer i =10;
		while(i>0) {
			map.put(i.toString(), i);
			i--;
		}
	}
	
	public static void main(String[] args) {
		//第一种（推荐）
		Iterator<Map.Entry<String, Object>>entryIterator = map.entrySet().iterator();
		while(entryIterator.hasNext()) {
			Map.Entry<String, Object> next = entryIterator.next();
			System.out.println("key:"+next.getKey()+"==>value:"+next.getValue());
		}
		
		//第二种，二次取值
		for(String key:map.keySet()) {
			System.out.println("key:"+key+"==>value:"+map.get(key));
		}
		
		//第三种，
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			System.out.println("key:"+entry.getKey()+"==>value:"+entry.getValue());
		}
		
		//第四种,只能遍历所有的value
		for(Object v: map.values()) {
			System.out.println("value:"+v);
		}
	}
}
