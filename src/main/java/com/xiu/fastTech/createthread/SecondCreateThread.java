package com.xiu.fastTech.createthread;

/**
 * 实现Runnable接口
 *
 */
public class SecondCreateThread implements Runnable{

	
	public void run() {
		
		System.out.println("...secondCreateThread..."+Thread.currentThread().getId());
	}
	
	public static void main(String[] args) {
		new Thread(new SecondCreateThread()).start();
	}

}
