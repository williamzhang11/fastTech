package com.xiu.fastTech.uncaughtthreadexception;


public class FirstUncaughtThread {

	
	public static void dealThread(Runnable r,Throwable t) {
		System.out.println(r.toString()+"="+t.getMessage());
	}
	
	public static void main(String[] args) {
		new Thread(new FirstUncaughtThread.Task()).start();
		
	}
	
	
	public  static class Task implements Runnable{

		public void run() {
			try {
				
				System.out.println("begin");
				System.out.println(3/0);
				System.out.println("end");
			}catch(Exception e) {
				dealThread(this, e);
			}
			
		}
		
	}
}
