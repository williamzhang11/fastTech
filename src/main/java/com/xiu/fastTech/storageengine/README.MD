# MyISAM和InnoDB的主要区别
存储引擎是如何存储数据，如何为存储数据建立索引以及如何更新，查询数据等技术的实现方法。
mysql中最常用的存储引擎为：InnoDB，MyISAM
MySQL5.5版本开始Innodb已经成为Mysql的默认引擎(之前是MyISAM)

## MyISAM和InnoDB的主要区别

	1.MyISAM是非事务安全型的，而InnoDB是事务安全型的
	2.MyISAM锁粒度是表级，而InnoDB支持行级锁定
	3.MyISAM支持全文类型索引，而InnoDB不支持全文索引
	4.InnoDB支持外键，而MyISAM不支持。对一个包含外键的InnoDB表转为MYISAM会失败；
	5. InnoDB是聚集索引，数据文件是和索引绑在一起的，必须要有主键，通过主键索引效率很高。
	但是辅助索引需要两次查询，先查询到主键，然后再通过主键查询到数据。因此，主键不应该过大，
	因为主键太大，其他索引也都会很大。而MyISAM是非聚集索引，数据文件是分离的，索引保存的是数据文件的指针。
	主键索引和辅助索引是独立的。
	6.InnoDB不保存表的具体行数，执行select count(*) from table时需要全表扫描。
	而MyISAM用一个变量保存了整个表的行数，执	行上述语句时只需要读出该变量即可，速度很快
	
	
	
	
	
	
	
	
	
	