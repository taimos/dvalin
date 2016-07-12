package de.taimos.dvalin.jpa;

/*
 * #%L
 * JPA support for dvalin using Hibernate
 * %%
 * Copyright (C) 2015 Taimos GmbH
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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringRepresentableType;
import org.hibernate.usertype.EnhancedUserType;
import org.joda.time.DateTime;

public class JodaDateTimeType implements EnhancedUserType, Serializable, StringRepresentableType<DateTime> {

    /**     */
    public static final JodaDateTimeType INSTANCE = new JodaDateTimeType();

    private static final long serialVersionUID = -7443774477681244536L;

    private static final int[] SQL_TYPES = new int[]{Types.TIMESTAMP};


    @Override
    public int[] sqlTypes() {
        return JodaDateTimeType.SQL_TYPES;
    }

    @Override
    public Class<DateTime> returnedClass() {
        return DateTime.class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return (x == y) || ((x != null) && x.equals(y));
    }

    @Override
    public int hashCode(final Object object) throws HibernateException {
        return object.hashCode();
    }

    @Override
    public Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session, final Object owner) throws HibernateException, SQLException {
        final Object timestamp = StandardBasicTypes.TIMESTAMP.nullSafeGet(rs, names[0], session, owner);
        if (timestamp == null) {
            return null;
        }

        return new DateTime(timestamp);
    }

    @Override
    public void nullSafeSet(final PreparedStatement st, final Object value, final int index, final SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, null, index, session);
        } else {
            StandardBasicTypes.TIMESTAMP.nullSafeSet(st, ((DateTime) value).toDate(), index, session);
        }
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(final Serializable cached, final Object value) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }

    @Override
    public String objectToSQLString(final Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toXMLString(final Object object) {
        return object.toString();
    }

    @Override
    public Object fromXMLString(final String string) {
        return new DateTime(string);
    }

    @Override
    public String toString(DateTime value) throws HibernateException {
        return value.toString();
    }

    @Override
    public DateTime fromStringValue(String string) throws HibernateException {
        return new DateTime(string);
    }
}
