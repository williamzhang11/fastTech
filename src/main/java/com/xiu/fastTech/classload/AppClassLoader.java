package com.xiu.fastTech.classload;

public class AppClassLoader {
	
	public static void main(String[] args) {
		
		String str = System.getProperty("java.class.path");
		System.out.println(str);
	}
}
