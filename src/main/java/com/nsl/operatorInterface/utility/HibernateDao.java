package com.nsl.operatorInterface.utility;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;


@SuppressWarnings("rawtypes")
@Component("hibernateDao")
public class HibernateDao implements IDao {
	private static final Log LOG = LogFactory.getLog(HibernateDao.class);

	 private SessionFactory sessionFactory;
	 @PersistenceContext
	 private EntityManager entityManager;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	public void delete(Object entity) throws DataAccessException {
		getSession().remove(entity);
	}
	
	public void remove(Object entity) throws DataAccessException {
		getSession().remove(entity);
	}

	public void deleteAll(Collection<? extends BaseEntity> entities) throws DataAccessException {
		for (BaseEntity entity : entities)
			getSession().remove(entity);
	}

	@SuppressWarnings("unchecked")
	public Object load(Class type, Serializable classId) throws DataAccessException {
		return getSession().get(type, classId);
	}

	@SuppressWarnings("deprecation")
	public void saveOrUpdate(Object entity) throws DataAccessException {
		getSession().saveOrUpdate(entity);
	}
	
	@SuppressWarnings("deprecation")
	public void mergeSaveOrUpdate(Object entity) throws DataAccessException 
	{
		getSession().saveOrUpdate(entity);
	}

	public void saveOrUpdateAll(Collection<? extends Object> entities) throws DataAccessException {
		for (Object entity : entities)

		{
			Session session = sessionFactory.openSession();
			Transaction transaction = session.beginTransaction();
			session.merge(entity);
			transaction.commit();
			session.close();
		}
	}

	public void flush() {
		getSession().flush();
	}

	public void clear() {
		getSession().clear();
	}

	public void merge(Object object) throws DataAccessException {
		getSession().merge(object);
	}

	public void refresh(Object object) {
		getSession().refresh(object);
	}
	@SuppressWarnings("unchecked")
	public Object get(Class type, Serializable classId) {
		return getSession().get(type, classId);
	}

	@SuppressWarnings("deprecation")
	public Object getCount(String sql) {
		try {
			Session session = getSession();
			Query query = session.createQuery(sql);
			List list = query.list();

			return  list.isEmpty() ? null : list.get(0);
		} catch (DataAccessException e) {
			LOG.info(e);
			return null;
		}
	}

	public Object getMaxValue(String sql) {
		try {
			Session session = getSession();
			@SuppressWarnings("deprecation")
			Query query = session.createQuery(sql);
			List list = query.list();

			return list.get(0);
		} catch (DataAccessException e) {
			LOG.info(e);
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	public List findByStartAndEndIndex(String sql, int startIndex, int endIndex) throws DataAccessException {
		Session session = getSession();
		Query query = session.createQuery(sql);
		query.setFirstResult(startIndex);
		query.setMaxResults(endIndex);
		return query.list();
	}

	@SuppressWarnings("deprecation")
	public Object getColumnValue(String sql) {
		try {
			Session session = getSession();
			Query query = session.createQuery(sql);
			List list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			} else {
				return null;
			}

		} catch (DataAccessException e) {
			LOG.info(e);
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	@Transactional
	public List find(String queryString, Object... values) throws DataAccessException {
		Query queryObject = getSession().createQuery(queryString);
		queryObject.setCacheable(true);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("deprecation")
	public Object findObject(String queryString, Object... values) throws DataAccessException {
		Query queryObject = getSession().createQuery(queryString);
		queryObject.setCacheable(true);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject.list().isEmpty() ? null : queryObject.list().get(0) ;
	}

	@SuppressWarnings("deprecation")
	public List findByFilter(String sql, String start, String limit) {
		Query query = getSession().createQuery(sql);

		query.setFirstResult(Integer.parseInt(start));
		query.setMaxResults(Integer.parseInt(limit));
		return query.list();
	}

	/**********************************************************************************
	 * Wrote this method to handle the transactions which have not gone through the
	 * OpenSessionInViewFilter For Example : In case of caching the user logout time
	 * Destroyed event which gets triggered with in the Servlet Container and during
	 * such events - OpenSessionInViewFilter wont be involved. So, we are creating
	 * the session object manually.
	 **********************************************************************************/
	public void openSessionAndExecuteQuery(String sql) {
		Session session = sessionFactory.openSession();
		@SuppressWarnings("deprecation")
		Query query = session.createQuery(sql);
		query.executeUpdate();
		session.close();
	}

	@Override
	public void evict(Object entity) {
		getSession().evict(entity);
	}

	public Session openSession() {
		return sessionFactory.openSession();
	}

	@Override
	public List findByNativeSql(String sql, Class entityClassName) {
		return new ArrayList<>();
	}

	@Override
	public List findByNativeSql(String sql, String cloumnName) {
		return new ArrayList<>();
	}

	@Override
	public List findByNativeSqlByStartAndLimit(String sql, Class entityClassName, String start, String limit) {
		return new ArrayList<>();
	}


	@Override
	public void executeSqlQuery(String sql) {
		LOG.info("");
 }

	@Override
	public List findBySql(String sql) {
		return new ArrayList<>();
	}

	@Override
	public int executeNativeQuery(String sql) {
		return 0;
	}

	@Override
	public Object getCountByNativeSQL(String sql) {
		return null;
	}

	@Override
	public Object getCountByCriteria(Class entityClassName) {
		return null;
	}

	@Override
	public Object getSumByCriteria(Class entityClassName, String columnName) {
		return null;
	}

	@Override
	public Object getMaxValueByCriteria(Class entityClassName, String columnName) {
		return null;
	}

	@Override
	public Object getMinValueByCriteria(Class entityClassName, String columnName) {
		return null;
	}

	@Override
	public Object getAvgByCriteria(Class entityClassName, String columnName) {
		return null;
	}

	@Override
	public List loadAll(Class type) throws DataAccessException {
		return new ArrayList<>();
	}

	@Override
	public List getLikeByCriteria(Class entityClassName, String columnName, String serachTerm) {
		return new ArrayList<>();
	}

	@Override
	public List getBetweenRecordsByCriteria(Class entityClassName, String columnName, String... values) {
		return new ArrayList<>();
	}

	@Override
	public List findByCriteria(Class entityClassName) {
		return new ArrayList<>();
	}

	@Override
	public List findObjectsByCriteria(Class entityClassName, String columnName, Object... values) {
		return new ArrayList<>();
	}

	@Override
	public Object findObjectByCriteria(Class entityClassName, String columnName, Object... values) {
		return null;
	}


}