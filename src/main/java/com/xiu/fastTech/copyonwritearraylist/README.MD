# CopyOnWriteArrayList底层原理

## 简介
	CopyOnWriteArrayList是java并发包中提供的并分容器，底层数据结构为数组，它是线程安全，读取无所的ArrayList，是一种读写分离的并发策略。
	称这个容器为写时复制。CopyOnWriteSet也是类似的。
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/copyonwritearraylist/image/copyonwritearraylist.jpg)

	
## 实现原理
	ArrayList是非线程安全的，而Vector虽然是线程安全的，但性能较差，而CopyOnWriteArrayList提供了一种读写分离的并发策略
	主要使用的场景是读多写少，允许并发读，读是无锁的,性能高。写操作会先加锁，然后将当前数组复制一份，然后在新的数组上操作，结束后将原来的数组引用
	指向新容器
## 优缺点
	优点：
	读性能很高，因为没有任何同步措施，适合读多写少的并发场景，java的list在遍历时，如果有其他线程对list修改，抛出ConcurrentModificationException异常
	而CopyOnWriteArrayList由于读写分离的思想，遍历和修改操作的是不同的list，因此不会抛出ConcurrentModifitionException异常
	缺点：
	1.占用内存很高，每次执行写操作都会将原数组拷贝一份，数据量大时，对内存压力大，可能引起频繁GC
	2.无法保证实时性，vection对读写操作均加锁同步，可保证读和写一致性。而CopyOnWriteArrayList由于读写分别作用在新老不同容器上，写操作的过程中，读取的
	依然是老的数据。
	
## 源码

### 基本结构

```
public class CopyOnWriteArrayList<E>
    implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
	//锁
    transient final ReentrantLock lock = new ReentrantLock();
	//数据数组
    private volatile transient Object[] array;
	//获取数组
    final Object[] getArray() {
        return array;
    }
	//设置数组，用旧数组的引用指向新数组
    final void setArray(Object[] a) {
        array = a;
    }
	
```

### add操作

将原数组拷贝一份，在新的副本上执行写操作，然后进行引用切换
```

    public boolean add(E e) {
    	//加锁，保证线程安全
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
        	//获取数组数据
            Object[] elements = getArray();
            //计算数组长度
            int len = elements.length;
            //拷贝原始数组得到新的数组，长度=1
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            //新的数组上增加元素
            newElements[len] = e;
            //将旧数组引用指向新数组，即新数组替换旧数组
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }

```
### remove


```
    public E remove(int index) {
    	//加锁
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            //获取删除索引位置的数据
            E oldValue = get(elements, index);
            
            int numMoved = len - index - 1;
            if (numMoved == 0)
            	//如果删除的是末端数据，拷贝len-1个数据到新的副本，再切换引用
                setArray(Arrays.copyOf(elements, len - 1));
            else {
            //否则将除了要删除的元素，拷贝到新副本，切换引用
                Object[] newElements = new Object[len - 1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index + 1, newElements, index,
                                 numMoved);
                setArray(newElements);
            }
            return oldValue;
        } finally {
            lock.unlock();
        }
    }

```
	
### get
无锁操作
```
    public E get(int index) {
        return get(getArray(), index);
    }
    
    private E get(Object[] a, int index) {
        return (E) a[index];
    }

```
	
	
	
	
	
	
	
	
	
	
	
	
