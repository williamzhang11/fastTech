package com.xiu.fastTech.threadlocalandinheritablethreadlocal;

public class InheritableThreadLocalTest {

	private static InheritableThreadLocal<Integer>inheritableThreadLocal = new InheritableThreadLocal<Integer>() {
		@Override
		protected Integer initialValue() {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	
	
	public static void main(String[] args) throws InterruptedException {
		InheritableThreadLocalTest in = new InheritableThreadLocalTest();
		inheritableThreadLocal.set(100);
		new ThreadTest(in).start();
		System.out.println("parent" + inheritableThreadLocal.get());
		InheritableThreadLocalTest.inheritableThreadLocal.set(InheritableThreadLocalTest.inheritableThreadLocal.get()+1);
		System.out.println("parent" + inheritableThreadLocal.get());
		
	}
	
	public static class ThreadTest extends Thread {
		
		InheritableThreadLocalTest inheritableThreadLocalTest;
		public ThreadTest(InheritableThreadLocalTest inheritableThreadLocalTest) {
			
			this.inheritableThreadLocalTest = inheritableThreadLocalTest;
		}
		public void run() {
			InheritableThreadLocalTest.inheritableThreadLocal.set(InheritableThreadLocalTest.inheritableThreadLocal.get()+1);
			System.out.println("childThread"+InheritableThreadLocalTest.inheritableThreadLocal.get());
			
		}
	}
}
