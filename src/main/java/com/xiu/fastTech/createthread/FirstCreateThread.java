package com.xiu.fastTech.createthread;

/**
 * 继承Thread类
 *
 */
public class FirstCreateThread extends Thread{

	@Override
	public void run() {
		int i=100;
		while(i>0) {
			
			System.out.println("...firstcreatethread..."+getName()+"num:"+i);
			i--;
		}
	}

	public static void main(String[] args) {
		
		new FirstCreateThread().start();
	}
}
