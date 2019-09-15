# ArrayList底层原理
 	底层数据结构：数组
 优点：
 	1.因为底层数据结构为数组，因此修改和查询效率高
 	2.可自动扩容（1.5倍）
缺点：
	1.插入和删除效率不高
	2.线程不安全
	
属性分析
```
	//默认初始化容量
	private static final int DEFAULT_CAPACITY = 10;
	//如果自定义容量0，默认用它来初始化ArrayList
    private static final Object[] EMPTY_ELEMENTDATA = {};
	//如果没有自定义容量，用它来初始化ArrayList
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
	//底层存储数据用到的数组
	//不允许变量序列化，transient已在序列化的类中实现
    transient Object[] elementData; // non-private to simplify nested class access
	//实际ArrayList集合大小
    private int size;
    。。。

```

ArrayList的增删操作基本上都是依靠Array.copy和system.arraycopy进行的。
添加和删除元素时，需要进行数据拷贝和移动，还要考虑是否需要扩容，效率比较低

取元素get()和存元素(set)的时间复杂度为O(1)
查找，插入和删除元素的效率不高，时间复杂度为O(n)





## add方法

```
    public boolean add(E e) {
    	//判断是否扩容
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        //添加到数组末尾
        elementData[size++] = e;
        return true;
    }
    
    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }
    
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
        //数组溢出需要扩容
            grow(minCapacity);
    }
    
    private void grow(int minCapacity) {
        // overflow-conscious code
        //记录之前数组长度
        int oldCapacity = elementData.length;
        //新数组大小为原来的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        //判断扩容后数组大小newCapacity能否装下minCapacity个元素
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
         //判断扩容新数组是否大于最大值
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        //数组复制，浅复制
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

```
##  add(int index, E element),添加到指定位置
```
    public void add(int index, E element) {
    	//1.校验合法性参数
        rangeCheckForAdd(index);
		//2.是否需要扩容
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        //3.数组添加位置，向后拷贝
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        //4.index处插入
        elementData[index] = element;
        size++;
    }

```
##  remove(int index)，删除指定位置元素：
```
	public E remove(int index) {
			//1.检查参数是否合法
	        rangeCheck(index);
			//2.增加修改次数，线程安全性验证，以快速失败
	        modCount++;
	        //3.记录需要删除的元素
	        E oldValue = elementData(index);
			//4.计算需要移动的数量
	        int numMoved = size - index - 1;
	        //5.将index后的元素向前移动一位
	        if (numMoved > 0)
	            System.arraycopy(elementData, index+1, elementData, index,
	                             numMoved);
	         //6.将原来末尾元素赋值null,方便GC回收
	        elementData[--size] = null; // clear to let GC do its work
	
	        return oldValue;
	}

```
public boolean remove(Object o),移除指定元素
```
	public boolean remove(Object o) {
		1.是否为空
        if (o == null) {
        //2.遍历第一个为null的元素
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                	//3.移除后返回true
                    fastRemove(index);
                    return true;
                }
        } else {
        //4.循环遍历第一个与0 equal（）的元素
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                //5.移除指定位置
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

	private void fastRemove(int index) {
		//记录更改 的次数，为了快速失败
        modCount++;
        //计算需要移动的数量
        int numMoved = size - index - 1;
        //从需要移除的位置依此向前拷贝
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                             numMoved);
        //元素末尾，赋值null,让gc回收
        elementData[--size] = null; // clear to let GC do its work
    }
```

## fail-fast机制
	fail-fast(快速失败机制)是java集合中的一种错误机制。
	当多线程对同一集合的内容进行操作时，可能产生fail-fast事件。如，当一个线程A通过iterator遍历某个集合的过程，若该集合的内容被其他线程修改；
那么线程A访问集合时，会抛出ConcurrentModificationException异常。单线程也有可能抛出该异常。目的是防止读取时，读到的是旧的数据，因为ArrayList不是线程安全的。

## ArrayList序列化

```
	transient Object[] elementData;
```	


	因为elementData使用transient关键字声明，该关键字修饰的变量不会参与到序列化，即使实现了Serializable接口，反序列化后仍为空值
ArrayList在序列化时调用writeObject（）方法，反序列化时使用readObject()恢复。因为elementData是一个缓存数组，它通常预留一些空间，等空间不足
再扩容，如果采用默认序列化，那这部分空间没有存储实际元素，因此采用writeObject、 readObject 序列化，只对有效的元素进行序列化，可以节省空间和时间。

扩容时使用：Arrays.copyOf()

其他：System.arrayCopy

Arrays.copyOf()不仅仅只是拷贝数组中的元素，在拷贝元素时，会创建一个新的数组对象。而System.arrayCopy只拷贝已经存在数组元素。

如果我们看过Arrays.copyOf()的源码就会知道，该方法的底层还是调用了System.arrayCopyOf()方法。

而且System.arrayCopy如果改变目标数组的值原数组的值也会随之改变。



















