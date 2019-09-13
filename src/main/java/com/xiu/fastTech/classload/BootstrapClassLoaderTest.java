package com.xiu.fastTech.classload;

public class BootstrapClassLoaderTest {

	public static void main(String[] args) {
		
		System.out.println(ClassLoader.getSystemClassLoader());
        System.out.println(ClassLoader.getSystemClassLoader().getParent());
        System.out.println(ClassLoader.getSystemClassLoader().getParent().getParent());
	}
}
