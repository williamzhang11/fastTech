package com.xiu.fastTech.concurrenthashmap;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapTest {
    public static void main(String[] args) {
    ConcurrentHashMap<String, Boolean> driverContentPourThreadMap = new ConcurrentHashMap<String, Boolean>();
    
    //System.out.println(driverContentPourThreadMap.containsKey("111"));
    
    driverContentPourThreadMap.remove("111");
    
    
    
    }
}
