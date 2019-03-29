package com.xiu.fastTech.memorymodel.image;

/**
 *32位jvm下long与double的赋值和读取不是原子的
 *可通过java -version查看jvm位数 
 *
 */
public class LongTest {
	static class NotAtomicity{
		private static long value;
		
		public static long getValue() {
			return value;
		}
		
		public static void setValue(long value) {
			NotAtomicity.value=value;
		}
	}
	
	static class WriteThread implements Runnable{
		
		private long value;
		
		public WriteThread(long value) {
			this.value = value;
		}
		public void run() {
			while (true) {
				NotAtomicity.setValue(value);
				Thread.yield();
			}
			
			
		}
	}
	
	static class ReadThread implements Runnable{
		
		public void run() {
			while (true) {
				long tmp =NotAtomicity.getValue();
				if(tmp!=100 && tmp!=200 && tmp!=-300 && tmp != -400) {
					System.out.println(tmp);
				}
				
				Thread.yield();
			}
			
		}
	}
	
	public static void main(String[] args) {
		new Thread(new WriteThread(100L)).start();
		new Thread(new WriteThread(200L)).start();
		new Thread(new WriteThread(-300L)).start();
		new Thread(new WriteThread(-400L)).start();
		new Thread(new ReadThread()).start();
	}
}
