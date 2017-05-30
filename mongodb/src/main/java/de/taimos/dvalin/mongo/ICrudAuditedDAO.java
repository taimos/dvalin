package de.taimos.dvalin.mongo;

import java.util.List;

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 * @param <E> an {@link AEntity}
 */
public interface ICrudAuditedDAO<E extends AAuditedEntity> extends ICrudDAO<E> {
	
	/**
	 * @param id the id of the document to get the history elements for
	 * @return the history elements for a document
	 */
	public List<E> findHistoryElements(String id);
	
}
