package com.xiu.fastTech.threadsynchronized;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class PipedTest {

	private  static volatile Boolean productRunning= true;
	private  static volatile Boolean consumerRunning= true;
	private static AtomicInteger count = new AtomicInteger(0);
	public static void main(String[] args) throws InterruptedException {
		
		PipedWriter pipedWriter =new PipedWriter();
		
		new Thread(new Consumer(pipedWriter)).start();
		Thread.sleep(1000);
		new Thread(new Product(pipedWriter)).start();
		Thread.sleep(100);
		
		productRunning=false;
		
		System.out.println("sum:"+count);
		
		
	}
	
	static class Product implements Runnable{
		
		PipedWriter pipedWriter=null;
		
		public Product(PipedWriter pipedWriter) {
			this.pipedWriter = pipedWriter;
			
		}

		public void run() {
			
			while(productRunning) {
				Integer data = count.addAndGet(1);
				System.out.println("product:"+data);
				try {
					pipedWriter.write(count.get());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			System.out.println("product end");
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	static class Consumer implements Runnable{
		PipedWriter pipedWriter=null;
		public Consumer(PipedWriter pipedWriter) {
			this.pipedWriter = pipedWriter;
		}
		
		public void run() {
			PipedReader prPipedReader = null;
			try {
				prPipedReader = new PipedReader(pipedWriter);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(consumerRunning) {
				try {
					System.out.println("consumer"+prPipedReader.read());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
}
