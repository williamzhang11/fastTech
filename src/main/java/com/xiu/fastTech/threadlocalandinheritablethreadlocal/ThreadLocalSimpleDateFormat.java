package com.xiu.fastTech.threadlocalandinheritablethreadlocal;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ThreadLocalSimpleDateFormat {

	
	private static ThreadLocal<SimpleDateFormat>threadLocalSimpleDateFormat = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			// TODO Auto-generated method stub
			return new SimpleDateFormat();
		}
	};
	
	
	private static SimpleDateFormat getSimpleDateFormat() {
		
		return threadLocalSimpleDateFormat.get();
	}
	
	public static void main(String[] args) {
		
		final ThreadLocalSimpleDateFormat threadLocalSimpleDateFormat = new ThreadLocalSimpleDateFormat();
		int i=10;
		while(i>0) {
			
			new Thread(new Runnable() {
				public void run() {
					SimpleDateFormat simpleDateFormat = threadLocalSimpleDateFormat.getSimpleDateFormat();
					System.out.println(simpleDateFormat);
					System.out.println(Thread.currentThread().getName() + ":" + simpleDateFormat.format(new Date()));
				}
			}).start();
			
			i--;
		}
		
	}
}
