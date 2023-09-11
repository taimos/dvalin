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

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * string member of a class definition
 */
@XmlType
public class StringMemberDef extends MemberDef implements ILabelMember {

    private Boolean useAsLabel = false;


    /**
     * @return the useAsLabel
     */
    @XmlAttribute(required = false)
    public Boolean getUseAsLabel() {
        return this.useAsLabel;
    }

    /**
     * @param useAsLabel the useAsLabel to set
     */
    public void setUseAsLabel(Boolean useAsLabel) {
        this.useAsLabel = useAsLabel;
    }

    @Override
    public Boolean useAsLabel() {
        return this.getUseAsLabel();
    }

    @Override
    public String getTypeAsString(boolean isInterface) {
        return String.class.getSimpleName();
    }
}
