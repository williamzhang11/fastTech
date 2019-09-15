# ThreadLocal与InheritableThreadLocals底层原理

ThreadLocal 最主要的特点就是，它不是为了解决多线程间同步问题而存在的（即多线程间共享变量），而是可以为每个线程创建属于自己的线程内的局部变量
ThreadLocal 通常被设置为private static字段，多个线程间的存储的数据副本都是相互独立的，不会相互影响。

## 特点：
	线程同步机制是多个线程共享同一个变量
	ThreadLocal是为每个线程创建一个单独的变量副本，每个线程都可以独立的改变自己的变量副本，而不会影响其他线程所对应的副本，这个副本的引用保存在Thread类中的
	ThreadLocal.ThreadLocalMap threadLocals。这个副本的修改和读取要通过ThreadLocal进行。
	ThreadLocal本身不存储值，它只是提供了一个在当前线程查找副本的keyThreadLocal包含在Thread中
	
ThreadLocal的关键是ThreadLocalMap，它底层实现是哈希表，解决冲突的方式是线性探测法，即经过哈希计算后，数组中有值，就往后查找，直到找到一个为空的位置，插入value.

## 重要方法

	get():返回当前线程对应的线程变量
	set(T value):设置当前线程局部变量的值
	initialValue():返回该线程局部变量的初始值
	remove():将当前线程局部变量的值删除


## 核心数据结构：
```
    static class ThreadLocalMap {

        static class Entry extends WeakReference<ThreadLocal<?>> {
            Object value;
			//key ThreadLocal,value是值
            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
    
    
```
## set方法：
```
    public void set(T value) {
        Thread t = Thread.currentThread();
        //第一步：获取当前线程中的ThreadLocalMap对象
        ThreadLocalMap map = getMap(t);
        //第二步：如果不为空，说明该线程存储过值
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }
    
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
    //内部类ThreadLocalMap的set方法
    private void set(ThreadLocal<?> key, Object value) {


            Entry[] tab = table;
            int len = tab.length;
            //第三步：根据ThreadLocal的哈希值，找到对应在数组中的位置
            int i = key.threadLocalHashCode & (len-1);
			//第四步：根据线性探测法寻找合适的位置
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                ThreadLocal<?> k = e.get();
				//第五步：key存在直接覆盖
                if (k == key) {
                    e.value = value;
                    return;
                }
				//第六步：k==null说明ThreadLocal对象已经被回收
                if (k == null) {
                //第七步：替换旧的值,并检查
                    replaceStaleEntry(key, value, i);
                    return;
                }
            }
			//第八步：ThreadLocal对应的实例不存在也没有旧元素，创建一个
            tab[i] = new Entry(key, value);
            int sz = ++size;
            //第九步：清理key=null的Entry，如果数字元素大于阈值，扩容
            if (!cleanSomeSlots(i, sz) && sz >= threshold)
                rehash();
        }
    
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
    

```
## get方法
```
	public T get() {
        Thread t = Thread.currentThread();
        //第一步：获取当前线程中的ThreadLocalMap值
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
    
    
    private Entry getEntry(ThreadLocal<?> key) {
       //第二步： 计算哈希值，定位数组中位置
            int i = key.threadLocalHashCode & (table.length - 1);
            Entry e = table[i];
            //第三步：如果非空，找到对应的值
            if (e != null && e.get() == key)
                return e;
            else
            //第四步：否则，在数组中依次向后查找，直到找到对应的key相同，在这过程中擦除，Entry实例存在但key为空的地方,防止内存泄露。
                return getEntryAfterMiss(key, i, e);
        }
        
     private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
            Entry[] tab = table;
            int len = tab.length;

            while (e != null) {
                ThreadLocal<?> k = e.get();
                if (k == key)
                    return e;
                if (k == null)
                    expungeStaleEntry(i);
                else
                    i = nextIndex(i, len);
                e = tab[i];
            }
            return null;
        }
        
```

## ThreadLocal中的内存泄漏

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/threadlocal/image/threadlocal.jpg)

	每个Thread对象都有个ThreadLocalMap，该map的key为ThreadLocal实例，它是一个弱引用，由于弱引用在gc时可能会被回收，但是value值确是一个强引用。导致
	value值无法被回收，造成内存泄露。

	解决方法：ThreadLocalMap中的set与get相关方法,在遇到key==null时，会置value=null.另外也可以显示调用Thread的remove方法。

# InheritableThreadLocal
	InheritableThreadLocal 可以在父线程创建子线程时将local中的值传递给子线程，但InheritableThreadLocal只会在创建线程的时候进行传值。
	但对于线程池的情况，线程由线程池创建好了，并缓存起来反复使用。因此会出现问题。

InheritableThreadLocal继承了ThreadLocal类，并重写了部分方法。

```
public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    protected T childValue(T parentValue) {
        return parentValue;
    }

    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }

    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}

```
get方法是使用的父类ThreadLocal中的get方法,其中调用getMap方法时，使用的是重写的getMap方法,返回的是Thread.inheritableThreadLocals变量。
```
    public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }

```
线程初始化时会将父线程中的值inheritableThreadLocals，拷贝到子线程中

```
    public Thread() {
        init(null, null, "Thread-" + nextThreadNum(), 0);
    }
    
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
        init(g, target, name, stackSize, null);
    }
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;

        Thread parent = currentThread();
        SecurityManager security = System.getSecurityManager();
        if (g == null) {

            if (security != null) {
                g = security.getThreadGroup();
            }

            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        g.checkAccess();

        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }

        g.addUnstarted();

        this.group = g;
        this.daemon = parent.isDaemon();
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        
        //若inheritableThreadLocals不为空则拷贝到子线程
        if (parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        this.stackSize = stackSize;

        tid = nextThreadID();
    }
    
    static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
        return new ThreadLocalMap(parentMap);
    }
    
            private ThreadLocalMap(ThreadLocalMap parentMap) {
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshold(len);
            table = new Entry[len];

            for (int j = 0; j < len; j++) {
                Entry e = parentTable[j];
                if (e != null) {
                    @SuppressWarnings("unchecked")
                    ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
                    if (key != null) {
                        Object value = key.childValue(e.value);
                        Entry c = new Entry(key, value);
                        int h = key.threadLocalHashCode & (len - 1);
                        while (table[h] != null)
                            h = nextIndex(h, len);
                        table[h] = c;
                        size++;
                    }
                }
            }
        }
    
    
```
而inheritableThreadLocals的初始化，是在父类set时进行的,其中createMap会调用重写的方法
```
    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }


	//重写方法
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }

```


## 线程池中使用InheritableThreadLocals
可以使用阿里开源的transmittable-thread-local



ThreadLocal的静态内部类ThreadLocalMap为每个Thread都维护了一个数组table，
ThreadLocal确定了一个数组下标，而这个下标就是value存储的对应位置。。


https://www.jianshu.com/p/3c5d7f09dfbd




























