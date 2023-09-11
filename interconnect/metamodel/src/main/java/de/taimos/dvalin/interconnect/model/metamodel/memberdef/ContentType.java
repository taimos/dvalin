package de.taimos.dvalin.interconnect.model.metamodel.memberdef;

/*
 * #%L
 * Dvalin interconnect metamodel for transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
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

import de.taimos.dvalin.interconnect.model.metamodel.xmladapter.ContentTypeAdapter;
import org.joda.time.DateTime;

import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * the supported content types
 */
@XmlType
@XmlJavaTypeAdapter(ContentTypeAdapter.class)
public enum ContentType {
    /**
     * boolean
     */
    Boolean(Boolean.class),
    /**
     * date
     */
    Date(DateTime.class),
    /**
     * decimal
     */
    Decimal(BigDecimal.class),
    /**
     * integer
     */
    Integer(Integer.class),
    /**
     * long
     */
    Long(Long.class),
    /**
     * string
     */
    String(String.class),
    /**
     * IVOs (requires ivoName, package name and version)
     */
    IVO(null),
    /**
     * enums (requires clazz and the pacakge attribute)
     */
    Enum(null),
    /**
     * UUID
     */
    UUID(UUID.class),
    /**
     * interconnect objects (requires clazz and the package attribute)
     */
    InterconnectObject(null),
    /**
     * LocalDate
     */
    LocalDate(java.time.LocalDate.class),
    /**
     * LocalTime
     */
    LocalTime(java.time.LocalTime.class),
    /**
     * ZonedDateTime
     */
    ZonedDateTime(java.time.ZonedDateTime.class);

    private final Class<?> type;

    ContentType(Class<?> type) {
        this.type = type;
    }

    /**
     * @return the type as string
     */
    public String getType() {
        return this.type == null ? null : this.type.getSimpleName();
    }


}
