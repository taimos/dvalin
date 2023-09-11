package de.taimos.dvalin.interconnect.model.metamodel.xmladapter;

/*-
 * #%L
 * Dvalin interconnect metamodel for transfer data model
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionType;

public class CollectionTypeAdapter extends XmlAdapter<String, CollectionType> {

    @Override
    public CollectionType unmarshal(String v) throws Exception {
        try {
            return CollectionType.valueOf(v);
        } catch (Exception e) {
            throw new JAXBException(e);
        }
    }

    @Override
    public String marshal(CollectionType v) throws Exception {
        return v.name();
    }

}
