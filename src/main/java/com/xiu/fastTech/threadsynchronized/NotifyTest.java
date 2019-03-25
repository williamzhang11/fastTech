package com.xiu.fastTech.threadsynchronized;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
/**
 * notify与notifyAll区别，notify会随机唤醒一个等待线程，notify会唤醒所有等待线程 
 *
 */
public class NotifyTest {

	private List<String> list;
	private Boolean isClose = false;
	
	public NotifyTest() {
		list = Collections.synchronizedList(new LinkedList<String>());
	}
	
	public void addItem(String id) {
		
		synchronized (this) {
			
			list.add(id);
			this.notify();
		}
	}
	
	public String remove() throws InterruptedException {
		synchronized (this) {
			while(list.isEmpty()&&!isClose) {
				System.out.println(Thread.currentThread().getName()+"进入等待");
				this.wait();
				System.out.println(Thread.currentThread().getName()+"解除等待");
			}
			if(list.size()>0) {
				
				return list.remove(0);
			}else {
				return "end";
			}
			
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		NotifyTest notifyTest = new NotifyTest();
		
		new Thread(new RemoveThread(notifyTest)).start();
		//new Thread(new RemoveThread(notifyTest)).start();
		
		Thread.sleep(1000);
		
		new Thread(new AddThread(notifyTest)).start();
		//new Thread(new AddThread(notifyTest)).start();
		//notifyTest.isClose=true;
		
	}
	
	
	static class RemoveThread implements Runnable{
		
		private NotifyTest notifyTest;
		public RemoveThread(NotifyTest notifyTest) {
			
			this.notifyTest = notifyTest;
		}
		public void run() {
			try {
				System.out.println(Thread.currentThread().getName()+"删除"+notifyTest.remove());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	static class AddThread implements Runnable{
		
		private NotifyTest notifyTest;
		public AddThread(NotifyTest notifyTest) {
			this.notifyTest = notifyTest;
		}
		
		public void run() {
			notifyTest.addItem("1");
		}
	}
	
}
