package com.xiu.fastTech.threadlocalandinheritablethreadlocal;

public class InheritableThreadLocalTest {

	private static InheritableThreadLocal<Integer>inheritableThreadLocal = new InheritableThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	
	public static void main(String[] args) {
		InheritableThreadLocal<Integer>inheritableThreadLocal = new InheritableThreadLocal<Integer>();
		
		
	}
	
	public static class ss {
		
	}
}
