# java反射机制
定义：指在运行状态中，能获取类所有的属性和方法信息并且可以动态调用对象中任意方法的一种机制。
## 反射的用处：
- 可动态获取类的基本信息，包括属性，方法等
- 可动态构造对象
- 可动态调用对象方法
- 可用于生成动态代理

使用反射技术的前提是要确保jvm加载该类的Class对象，即获取Class对象。

## class对象：
class对象时一个特殊的对象，它是用来创建其他对象的对象。class对象包含了类相关的信息，它是编译一个类时产生的，保存在.class文件（字节码文件）中。

当使用new创建一个对象时，jvm的类加载器会自动加载该类的class对象到jvm中，然后jvm会根据class对象，创建实例对象。无论创建多少个实例对象都对应同一个class对象。
改class对象只能由jvm创建和加载。


## 手动获取class对象的三种方式：

- Class.forName("包名+类名")方法
- 实例对象的getClass()方法,new MyClass.getClass()
- Class字面量,MyClass.class
- 针对包装类型的TYPE字段，Boolean.TYPE


## 典型的类加载过程

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/reflect/20170430160610299.png)

- 加载：jvm类加载器，利用字节码文件创建Class对象
- 链接：验证，字节码的安全性和完整性。准备，为静态成员分配存储空间。解析当前类对其他类的引用。
- 初始化：执行静态代码块以及静态变量

其中，实例类的getClass方法和Class.forName("包名+类名")方法会触发类的初始化阶段,但是如果之前已经初始化过，并不会再次触发

获取Class对象后，就可以利用reflect包提供的方法进行需要的操作了。

## 反射机制中常用功能的有：
- Constructor类，表示Class对象所表示的构造方法，可以用来动态创建对象
- Field类，表示Class对象所表示的成员变量，可以用来动态修改成员变量的属性值
- Method类，表示Class对象所表示类的成员方法，可以用来动态调用对象的方法



























