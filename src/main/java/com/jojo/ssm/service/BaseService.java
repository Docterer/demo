package com.jojo.ssm.service;

import java.util.List;

import tk.mybatis.mapper.entity.Example;

public interface BaseService<T> {

	/**
	 * ����ģ��
	 * 
	 * @return
	 */
	Example buildExample();

	/**
	 * ��
	 */
	/**
	 * nullֵҲ����ȥ
	 * 
	 * @param entity
	 * @return
	 */
	int insert(T entity);

	/**
	 * nullֵ���壬��ʹ�����ݿ��Ĭ��ֵ
	 * 
	 * @param entity
	 * @return
	 */
	int insertSelective(T entity);

	/**
	 * ɾ
	 */
	int deleteByPrimaryKey(Object key);

	int deleteByExample(Object example);

	/**
	 * ��
	 */
	int selectCountByExample(Object example);

	int selectCount(T entity);

	T selectOneByPrimaryKey(Object key);

	T selectOneByExample(Object example);

	List<T> selectByExample(Object example);

	List<T> selectAll();

	/**
	 * ��
	 */
	int updateByPrimaryKey(T entity);

	int updateByPrimaryKeySelective(T entity);

	int updateByExample(T entity, Object example);

	int updateByExampleSelective(T entity, Object example);
}
