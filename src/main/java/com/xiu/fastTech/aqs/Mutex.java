package com.xiu.fastTech.aqs;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Mutex implements Lock,Serializable{
	
	
	private static class Sync extends AbstractQueuedSynchronizer{
		
		//判断是否锁定
		protected boolean isHeldExclusively() {
			return getState()==1;
		}
		//尝试获取资源，成功返回true,否则返回false
		public boolean tryAcquire(int acquires) {
			assert acquires ==1;
			if(compareAndSetState(0, 1)) {//state为0设置1，不可重入
				setExclusiveOwnerThread(Thread.currentThread());
				return true;
			}
			return false;
		}
		
		public boolean tryRealse(int releases) {
			assert releases ==1;
			if(getState() == 0) {
				throw new IllegalMonitorStateException();
			}
			setExclusiveOwnerThread(null);
			setState(0);
			return true;
		}
	}
	
	private final Sync sync = new Sync();

	public void lock() {
		sync.acquire(1);
	}

	public void lockInterruptibly() throws InterruptedException {
		
	}

	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	public void unlock() {
		sync.release(1);
	}

	public Condition newCondition() {
		return null;
	}

	
}
