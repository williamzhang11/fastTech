package com.xiu.fastTech.memorymodel.image;

import java.util.concurrent.CountDownLatch;

public class PrimitiveTest {

	public static void main(String[] args) throws InterruptedException {
		
		new CountDownLatchThread(10).initStart();
	}
	
	
	static class CountDownLatchThread extends CountDownLatch{

		public CountDownLatchThread(int count) {
			super(count);
		}
		
		public void initStart() throws InterruptedException {
			
			for (int i=0;i<10;i++) {
				new Thread(new IntTest(this)).start();
			}
			this.await();
			System.out.println(AddOperator.getBegin());
		}
		
	}
	
	static class IntTest  implements Runnable{
		CountDownLatchThread countDownLatchThread;
		public IntTest(CountDownLatchThread countDownLatchThread) {
			this.countDownLatchThread = countDownLatchThread;
		}

		public void run() {
			synchronized (IntTest.class) {
				for(int i=0;i<10;i++) {
					AddOperator.addOne();
				}
			}
			countDownLatchThread.countDown();
		}
		
	}
	
	static class AddOperator{
		
		static int begin =0;
		
		public static void addOne() {
			begin++;
		}
		
		public static int getBegin() {
			return begin;
		}
	}
}
