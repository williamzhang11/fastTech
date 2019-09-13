package com.xiu.fastTech.classload;

public class ExtClassLoader {

	public static void main(String[] args) {
		String str = System.getProperty("java.ext.dirs");
	    System.out.println(str);
	}
}
