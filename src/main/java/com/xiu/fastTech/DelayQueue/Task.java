package com.xiu.fastTech.DelayQueue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


class Task<T extends Runnable> implements Delayed{

	//到期时间
	private  long time;
	
	private T task;
	
	private static AtomicLong atomicLong = new AtomicLong(0);
	
	private long n;
	
	public Task(long timeout,T t) {
		
		this.time = System.nanoTime()+timeout;
		this.task = t;
		this.n= atomicLong.getAndIncrement();
	}
	
	public int compareTo(Delayed o) {
		
		if(o == this) {
			return 0;
		}
		if(o instanceof Task) {
			Task xDelayTask = (Task)o;
			long diff = time-xDelayTask.time;
			if(diff <0) {
				return -1;
			}else if(diff >0){
				return 1;
			}else if (n < xDelayTask.n) {
				return -1;
			}else {
				return 1;
			}
		}
		long d= (getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS));
		
		return (d == 0) ? 0 : (( d < 0 ) ? -1 : 1);
	}

	public long getDelay(TimeUnit unit) {
		
		return unit.convert(this.time-System.nanoTime(), TimeUnit.NANOSECONDS);
	}
	
	public T getTask() {
		return this.task;
	}

	@Override
	public int hashCode() {
		return task.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Task) {
			return obj.hashCode()==hashCode()?true:false;
		}
		return false;
	}
	
}
