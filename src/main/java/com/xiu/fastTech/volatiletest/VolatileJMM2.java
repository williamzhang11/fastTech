package com.xiu.fastTech.volatiletest;

public class VolatileJMM2 {

	private static volatile Integer i;
	public static void main(String[] args) {
		
		i =100;
		System.out.println("i="+i);
		
	}
}
