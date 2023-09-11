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

import org.joda.time.DateTime;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import java.time.ZonedDateTime;

/**
 * Date clazz member
 */
@XmlType
public class DateMemberDef extends MemberDef {

    private Boolean jodaMode = true;

    /**
     * default constructor
     */
    public DateMemberDef() {
        //default constructor
    }

    /**
     * @param javaMode the java mode
     */
    public DateMemberDef(boolean javaMode) {
        this.jodaMode = javaMode;
    }

    @Override
    public String getTypeAsString(boolean isInterface) {
        if (Boolean.TRUE.equals(this.jodaMode)) {
            return DateTime.class.getSimpleName();
        }
        return ZonedDateTime.class.getSimpleName();
    }

    /**
     * @return the jodaMode
     */
    @XmlAttribute(required = false)
    public Boolean getJodaMode() {
        return this.jodaMode;
    }

    /**
     * @param jodaMode the jodaMode to set
     */
    public void setJodaMode(Boolean jodaMode) {
        this.jodaMode = jodaMode;
    }
}
