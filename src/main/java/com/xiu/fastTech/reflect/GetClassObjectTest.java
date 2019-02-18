package com.xiu.fastTech.reflect;

/**
 * @author zxy
 * 获取Class对象
 * 如果之前触发过静态代码块的初始化，第二次不会再次触发
 */
public class GetClassObjectTest {

	public static void main(String[] args) {
		
		//第一种方法，不会初始化，静态代码块
		Class<User> userClass1 = User.class;
		System.err.println(userClass1);
		
		//第二种方法
		Class<?> userClass2 = null;
		
		try {
			userClass2 = Class.forName("com.xiu.fastTech.reflect.User");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(userClass2);
		
		//第三种方法
		User user = new User();
		Class<?> classUser3 = user.getClass();
		
		System.err.println(classUser3);
		
	}
}
