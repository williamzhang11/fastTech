package com.xiu.fastTech.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;


public class ReferenceTest {

	public static void main(String[] args) throws InterruptedException {
		
		//软引用
		/*User user1 = new User(1,"zhangsan");
		SoftReference<User>softReference = new SoftReference<User>(user1);
		System.gc();
		System.out.println("软引用"+softReference.get());
		
		//弱引用
		User user2 = new User(2,"lisi");
		WeakReference<User> weakReference = new WeakReference<User>(user2);
		user2=null;//强引用置空
		System.gc();
		System.out.println("弱引用"+weakReference.get());*/
		
		//虚引用,被回收之前会加入到引用队列
		/*ReferenceQueue<User> referenceQueue = new ReferenceQueue<User>();
		User user3 = new User(3,"wangwu");
		PhantomReference<User>phantomReference = new PhantomReference<User>(user3, referenceQueue);
		user3 =null;
		
		while(true) {
			 System.out.printf("pf.get() = %d, isEnqueued: %b\r\n", phantomReference.get(), phantomReference.isEnqueued());
			if(phantomReference.isEnqueued()) {
				break;
			}
			System.gc();
		}*/
		//弱引用第二种方式
		ReferenceQueue<User>referenceQueue = new ReferenceQueue<ReferenceTest.User>();
		User user4 = new User(4,"zhaoliu");
		WeakRefereceUser  s= new WeakRefereceUser(user4,referenceQueue);
		System.out.println(s.get());
		int i=0;
		while(true) {
			if(s.get()!=null) {
				i++;
				System.out.println("循环次数"+i);
			}else {
				
				System.out.println("已经被清空");
				break;
			}
		}
		System.out.println(s.isEnqueued());
		
		
	}
	
	static class WeakRefereceUser extends WeakReference<User>{

		public WeakRefereceUser(User referent) {
			super(referent);
		}
		
		public WeakRefereceUser(User referent, ReferenceQueue<User> q) {
	        super(referent, q);
	    }
		
	}
	
	static class User {
		
		public User(Integer id, String name) {
			super();
			this.id = id;
			this.name = name;
		}
		private Integer id;
		private String name;
		
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		@Override
		public String toString() {
			return "user [id=" + id + ", name=" + name + "]";
		}
		
		

		
	}
}
