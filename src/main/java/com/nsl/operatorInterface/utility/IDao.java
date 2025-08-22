package com.nsl.operatorInterface.utility;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

@SuppressWarnings("rawtypes")
public interface IDao {

	void delete(Object entity) throws DataAccessException;
	
	void remove(Object entity) throws DataAccessException;

	Object load(Class type, Serializable classId) throws DataAccessException;

	Object get(Class type, Serializable classId);

	void saveOrUpdate(Object entity) throws DataAccessException;
	void mergeSaveOrUpdate(Object entity) throws DataAccessException;

	void merge(Object entity) throws DataAccessException;

	void refresh(Object entity) throws DataAccessException;

	void flush();

	void clear();

	void evict(Object entity);

	public Object getCount(String sql);

	public Object getMaxValue(String sql);

	public List findByNativeSql(String sql, Class entityClassName);

	public List findByNativeSql(String sql, String cloumnName);

	public List findByStartAndEndIndex(String sql, int startIndex, int endIndex) throws DataAccessException;

	public Object getColumnValue(String sql);

	public List findByNativeSqlByStartAndLimit(String sql, Class entityClassName, String start, String limit);

	public void deleteAll(Collection<? extends BaseEntity> entities) throws DataAccessException;

	public List find(String query, Object... values) throws DataAccessException;

	public Object findObject(String query, Object... values) throws DataAccessException;

	public List loadAll(Class type) throws DataAccessException;

	public void saveOrUpdateAll(Collection<? extends Object> entities) throws DataAccessException;

	public List findByFilter(String sql, String start, String limit);

	public void executeSqlQuery(String sql);

	public void openSessionAndExecuteQuery(String sql);

	public List findBySql(String sql);

	public int executeNativeQuery(String sql);

	public Object getCountByNativeSQL(String sql);

	public Object getCountByCriteria(Class entityClassName);

	public Object getSumByCriteria(Class entityClassName, String columnName);

	public Object getMaxValueByCriteria(Class entityClassName, String columnName);

	public Object getMinValueByCriteria(Class entityClassName, String columnName);

	public Object getAvgByCriteria(Class entityClassName, String columnName);

	public List getLikeByCriteria(Class entityClassName, String columnName, String serachTerm);

	public List getBetweenRecordsByCriteria(Class entityClassName, String columnName, String... values);

	public List findByCriteria(Class entityClassName);

	public List findObjectsByCriteria(Class entityClassName, String columnName, Object... values);

	public Object findObjectByCriteria(Class entityClassName, String columnName, Object... values);

}
