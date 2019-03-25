package com.xiu.fastTech.threadsynchronized;

/**
 * 
 *
 */
public class CalculatorAndReadThread {
	
	
	public static void main(String[] args) {
		
		Calculator calculator = new Calculator();
		
		new Thread(new ReadThread(calculator)).start();
		
		new Thread(calculator).start();
	}

	static class Calculator implements Runnable{
		
		private int sum = 0;
		private Boolean isSignal = false;//是否通知
		public void run() {
			
			synchronized (this) {
				int i=100;
				
				while(i>0) {
					sum+=i;
					i--;
				}
				isSignal = true;
				this.notify();
			}
		}
	}
	
	static class ReadThread implements Runnable{
		Calculator calculator;
		
		public ReadThread( Calculator calculator) {
			this.calculator = calculator;
		}
		
		public void run() {
			
			synchronized (calculator) {
				
				while(!calculator.isSignal) {
					try {
						calculator.wait();
						System.out.println("sum="+calculator.sum);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
}
