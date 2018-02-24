package com.jojo.ssm.service;

import java.util.List;

import tk.mybatis.mapper.entity.Example;

public interface BaseService<T> {

	/**
	 * 创建模板
	 * 
	 * @return
	 */
	Example buildExample();

	/**
	 * 增
	 */
	/**
	 * null值也会插进去
	 * 
	 * @param entity
	 * @return
	 */
	int insert(T entity);

	/**
	 * null值不插，将使用数据库的默认值
	 * 
	 * @param entity
	 * @return
	 */
	int insertSelective(T entity);

	/**
	 * 删
	 */
	int deleteByPrimaryKey(Object key);

	int deleteByExample(Object example);

	/**
	 * 查
	 */
	int selectCountByExample(Object example);

	int selectCount(T entity);

	T selectOneByPrimaryKey(Object key);

	T selectOneByExample(Object example);

	List<T> selectByExample(Object example);

	List<T> selectAll();

	/**
	 * 改
	 */
	int updateByPrimaryKey(T entity);

	int updateByPrimaryKeySelective(T entity);

	int updateByExample(T entity, Object example);

	int updateByExampleSelective(T entity, Object example);
}
