# 数据库锁

![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/dblock/image/sqllock.jpg)

## 数据库中隐式设置的锁
	1.对于update,delete,insert语句，innodb自动给涉及数据集加排他锁（X）
	2.而MyISAM在执行查询语句select前，会自动给涉及的表加读锁，在执行更新操作（update,
		delete,insert）前，自动给涉及的表加写锁。myisam并不支持事务，同时锁也是表锁，
		因此不会出现死锁的情况

	从锁的粒度，可分为2大类：
	1.表锁
		开销小，加锁快；不会出现死锁；锁定粒度大，发生锁冲突概率高，并发度低
	2.行锁
		开销大，加锁慢；会出现死锁；锁定力度小，发生所冲突概率低，并发度高
		
	不同存储引擎支持的锁粒度不一样：
	1.Innodb行锁，表锁都支持
	2.myisam只支持表锁
	
	InnodDB只有通过索引条件检索数据才使用行级锁，否则，innodb使用表锁
	因此innodb行锁是基于索引的。
	
## 表锁
	表锁分为2种模式
	表读锁（Table Read Lock）
	表写锁（Table Write Lock）
![image](https://github.com/williamzhang11/fastTech/blob/master/src/main/java/com/xiu/fastTech/dblock/image/tablelock.jpg)

	上图可看到表读锁和表写锁环境下：读读不阻塞，读写阻塞，写写阻塞
	
	读读不阻塞：当前用户在读数据，其他用户也在读数据，不会加锁
	读写阻塞：当前用户在读数据，其他用户不能修改当前用户读的数据，会加锁
	写写阻塞：当前用户在修改数据，其他用户不能修改当前用户正在修改的数据，会加锁
	
	如果某个线程想获取读锁，另一个进程想获取写锁，mysql里写锁优于读锁
	
## 行锁
	Mysql种一般使用InnoDB存储引擎，它与MyISAM有2个本质区别
		InnoDB支持行锁
		InnoDB支持事务
	很少手动加表锁，即使innodB不走索引，加表锁也是自动的
	InnoDB实现2种类型的行锁
	共享锁（S锁）：允许多个事务去读一行
		也叫读锁：读锁是共享的，多个事务可同时读取同一个资源，但不允许其他事务修改
	排他锁（X锁）：允许获得排他锁的事务更新数据，阻止其他事务取得相同数据集的共享读锁和排他写锁。
		也叫写锁：写锁会阻塞其他写锁和读锁
	
	为了允许行锁和表锁共存，实现多粒度锁机制，innoDB还有2种内部使用的意向锁，这2种意向锁都是表锁
	意向共享锁（IS）:事务打算给数据行加共享锁，必须先取得该表IS锁
	意向排他锁（IX）:事务打算给数据行加排他锁，必须取得该表的IX锁
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
