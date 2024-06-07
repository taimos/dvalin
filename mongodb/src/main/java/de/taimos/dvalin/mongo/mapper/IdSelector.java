package de.taimos.dvalin.mongo.mapper;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public interface IdSelector<T> {
    boolean isId(T a);
    boolean isObjectId(T a);
}
