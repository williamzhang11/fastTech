# 乐观锁的2种实现

## 数据版本（Version）记录机制
	为数据增加一个版本标识，一般是通过为数据库表增加一个数字类型的 “version” 字段来实现。当读取数据时，将version字段的值一同读出，
	数据每更新一次，对此version值加一。当我们提交更新的时候，判断数据库表对应记录的当前版本信息与第一次取出来的version值进行比对，
	如果数据库表当前版本号与第一次取出来的version值相等，则予以更新，否则认为是过期数据
	
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/optimisticlock/image/lockconflict.jpg)
	
## 字段类型使用时间戳

	和上面的version类似，也是在更新提交的时候检查当前数据库中数据的时间戳和自己更新前取到的时间戳进行对比，如果一致则OK，否则就是版本冲突。