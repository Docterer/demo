###sqlserver
// 查询表的字段类型
SELECT * FROM INFORMATION_SCHEMA.columns WHERE TABLE_NAME='lpdsuser';

// 查询被锁住的表
SELECT
	request_session_id spid,
	OBJECT_NAME (
		resource_associated_entity_id
	) tableName
FROM
	sys.dm_tran_locks
WHERE
	resource_type = 'OBJECT'
	

	
	
	
	

	
	
	
	
	
	
	
	
	
	
	

###mysql
// 显示所有数据库
show databases;

// 创建与删除数据库
create database 数据库名; 		（对，没错，不要引号）
drop database 数据库名;			（对，没错，不要引号）