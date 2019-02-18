package com.xiu.fastTech.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
//反射中常用的功能方法
public class CriticalFunctionTest {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		//第一步，获取Class对象
		Class<?> userClass = null;
		try {
			userClass = Class.forName("com.xiu.fastTech.reflect.User");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//第二步，使用反射中常用的功能方法
		//初始化对象,无参数构造函数
		User user1 =null;
		user1= (User)userClass.newInstance();
		user1.setAge(10);
		user1.setName("zhangsan");
		System.out.println(user1);
		
		//初始化对象，有参数构造函数
		Constructor<?> userConstructor= null;
			userConstructor = userClass.getConstructor(Integer.class,String.class);
		User user2 =null;
			user2 = (User)userConstructor.newInstance(20,"lisi");
		System.out.println(user2);
		
		//获取类中属性
		//共有属性
		Field field = userClass.getField("address");
		System.out.println("fieldPublic:"+field);
		
		//包括公有和私有属性
		Field field2[] = userClass.getDeclaredFields();
		for(Field f : field2) {
			System.out.println(f);
		}
		//调用对象方法赋值 
		User user = (User)userClass.newInstance();
		Method method1 = userClass.getMethod("setAge", Integer.class);
		Method method2 = userClass.getMethod("setName", String.class);
		method1.invoke(user, 30);
		method2.invoke(user, "wangwu");
		System.out.println(user);
		//调用对象方法取值
		Method method3 = userClass.getDeclaredMethod("getAge");
		Integer age3 = (Integer)method3.invoke(user);
		System.out.println(age3);
		
	}
}


