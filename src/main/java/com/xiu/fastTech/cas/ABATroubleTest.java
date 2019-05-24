package com.xiu.fastTech.cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

public class ABATroubleTest {
	
	private static AtomicInteger atomicInteger = new AtomicInteger(100);
	private static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<Integer>(100, 1);
	
	
	public static void main(String[] args) throws InterruptedException {
		
		Thread at1 = new Thread(new Runnable() {
			
			public void run() {
				atomicInteger.compareAndSet(100, 110);
				atomicInteger.compareAndSet(110, 100);
			}
		});
		
		Thread at2 = new Thread(new Runnable() {
			
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println("AtomicInteger:"+atomicInteger.compareAndSet(100, 120));
			}
		});
		
		at1.start();
		at2.start();
		at1.join();
		at2.join();
		
		Thread tsf1 =new Thread(new Runnable() {
			
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				atomicStampedReference.compareAndSet(100, 110, atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);
				atomicStampedReference.compareAndSet(110, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp()+1);
				
				System.err.println("stamp"+atomicStampedReference.getStamp());
				
			}
		});
		
		Thread tsf2 =new Thread(new Runnable() {
			
			public void run() {
				int stamp = atomicStampedReference.getStamp();
				
				System.out.println(stamp);
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("AtomicStampedReference:" +atomicStampedReference.compareAndSet(100,120,stamp,stamp + 1));
			}
			
		});
		
		 tsf1.start();
	     tsf2.start();
		
		
		
		
	}
}
