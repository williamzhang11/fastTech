package com.xiu.fastTech.DelayQueue;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.management.remote.TargetedNotification;

/**
 * 后台守护线程不断执行检测工作
 * @author PC
 *
 */
public class TaskQueueThread {

	Executor executor = Executors.newFixedThreadPool(20);
	//守护线程
	private Thread daemonThread;
	
	private DelayQueue<Task>t = new DelayQueue<Task>();
	
	private TaskQueueThread() {
	}
	
	private static class LazyHolder{
		private static TaskQueueThread taskQueueThread = new TaskQueueThread();
	}
	
	public static TaskQueueThread getInstance() {
		return LazyHolder.taskQueueThread;
	}
	
	
	public void init() {
		daemonThread = new Thread(new Runnable() {
			public void run() {
				getInstance().execute();
			}
		});
		//daemonThread.setDaemon(true);
		daemonThread.setName("Task Queue Daemon Thread");
		daemonThread.start();
	}
	
	private void execute() {
		
		while (true) {
			System.err.println("1111");
			
			try {
				Task t1 = t.take();
				
				if(t1 !=null) {
					
					Runnable task = t1.getTask();
					
					if(task == null) {
						continue;
					}
					executor.execute(task);
				}
			}catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
	}
	
	public void put(long time,Runnable task) {
		
		long nanoTime = TimeUnit.NANOSECONDS.convert(time, TimeUnit.MILLISECONDS);
		Task kTask = new Task<Runnable>(nanoTime, task);
		t.put(kTask);
	}
	
	public Boolean endTask(Task<Runnable> task) {
		return t.remove(task);
	}
	
	
}
