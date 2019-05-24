package com.xiu.fastTech.cas;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerTest {

	public static void main(String[] args) {
		AtomicInteger atomicInteger = new AtomicInteger(10);
		System.out.println(atomicInteger.addAndGet(100));
		
		
	}
}
