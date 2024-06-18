package de.taimos.dvalin.mongo;

/*
 * #%L
 * Spring DAO Mongo
 * %%
 * Copyright (C) 2013 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Iterator;

import org.jongo.ResultHandler;

import com.mongodb.DBObject;

/**
 * Copyright 2015 Hoegernet<br>
 * <br>
 * {@link Iterable} which converts the objects on-the-fly via the given {@link ResultHandler}
 *
 * @param <T>
 * @author Thorsten Hoeger
 */
final class ConverterIterable<T> implements Iterable<T> {

    private final Iterator<DBObject> iterator;
    private final ResultHandler<T> conv;


    ConverterIterable(Iterator<DBObject> iterator, ResultHandler<T> conv) {
        this.iterator = iterator;
        this.conv = conv;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            @Override
            public void remove() {
                ConverterIterable.this.iterator.remove();
            }

            @Override
            public T next() {
                return ConverterIterable.this.conv.map(ConverterIterable.this.iterator.next());
            }

            @Override
            public boolean hasNext() {
                return ConverterIterable.this.iterator.hasNext();
            }
        };
    }
}
