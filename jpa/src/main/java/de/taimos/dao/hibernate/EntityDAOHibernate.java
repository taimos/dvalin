package de.taimos.dao.hibernate;

/*
 * #%L
 * Hibernate DAO for Spring
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import de.taimos.dao.IEntity;
import de.taimos.dao.IEntityDAO;

/**
 * @author hoegertn
 * 
 * @param <E> the entity type
 * @param <I> the id type
 */
public abstract class EntityDAOHibernate<E extends IEntity<I>, I> implements IEntityDAO<E, I> {
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	
	@Override
	@Transactional
	public E save(final E element) {
		return this.entityManager.merge(element);
	}
	
	@Override
	@Transactional
	public void delete(final E element) {
		this.entityManager.remove(this.entityManager.merge(element));
	}
	
	@Override
	@Transactional
	public void deleteById(final I id) {
		final E element = this.findById(id);
		if (element == null) {
			throw new EntityNotFoundException();
		}
		this.entityManager.remove(element);
	}
	
	@Override
	public E findById(final I id) {
		return this.entityManager.find(this.getEntityClass(), id);
	}
	
	@Override
	public List<E> findList(final int first, final int max) {
		final TypedQuery<E> query = this.entityManager.createQuery(this.getFindListQuery(), this.getEntityClass());
		if (first >= 0) {
			query.setFirstResult(first);
		}
		if (max >= 0) {
			query.setMaxResults(max);
		}
		return query.getResultList();
	}
	
	@Override
	public List<E> findList() {
		return this.findList(-1, -1);
	}
	
	/**
	 * @param query
	 * @param params
	 * @return the entity or null if not found or more than one found
	 */
	protected final E findByQuery(final String query, final Object... params) {
		final List<E> list = this.findListByQuery(query, params);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * @param query
	 * @param params
	 * @return the entity or null if not found or more than one found
	 */
	protected final Object[] findGenericByQuery(final String query, final Object... params) {
		final List<Object[]> list = this.findGenericListByQuery(query, params);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	protected final List<E> findListByQuery(final String query, final Object... params) {
		return this.findListByQueryLimit(query, -1, -1, params);
	}
	
	protected final List<Object[]> findGenericListByQuery(final String query, final Object... params) {
		return this.findGenericListByQueryLimit(query, -1, -1, params);
	}
	
	protected final List<Object[]> findGenericListByQueryLimit(final String query, final int first, final int max, final Object... params) {
		return this.findListByQueryLimit(query, Object[].class, first, max, params);
	}
	
	protected final List<E> findListByQueryLimit(final String query, final int first, final int max, final Object... params) {
		return this.findListByQueryLimit(query, this.getEntityClass(), first, max, params);
	}
	
	private <T> List<T> findListByQueryLimit(final String query, Class<T> clazz, final int first, final int max, final Object... params) {
		final TypedQuery<T> tq = this.entityManager.createQuery(query, clazz);
		for (int i = 0; i < params.length; i++) {
			tq.setParameter(i + 1, params[i]);
		}
		if (first >= 0) {
			tq.setFirstResult(first);
		}
		if (max >= 0) {
			tq.setMaxResults(max);
		}
		return tq.getResultList();
	}
	
	/**
	 * Override in sub-class if not "id"
	 * 
	 * @return the id field
	 */
	protected String getIdField() {
		return "id";
	}
	
	/**
	 * Override in sub-class if not "FROM 'EntityName'"
	 * 
	 * @return the select query
	 */
	protected String getFindListQuery() {
		return "FROM " + this.getEntityClass().getSimpleName();
	}
	
}
