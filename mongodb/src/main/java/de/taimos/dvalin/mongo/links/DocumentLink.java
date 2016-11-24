package de.taimos.dvalin.mongo.links;

/*
 * #%L
 * Spring DAO Mongo
 * %%
 * Copyright (C) 2013 - 2015 Taimos GmbH
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

/**
 * Link to another document of a {@link AReferenceableEntity}<br>
 * It is stored as an object containing the target class, the objectId of the target and a label to avoid joining the document for display
 * purpose.
 *
 * @param <T> the target type
 * @author Thorsten Hoeger
 */
public class DocumentLink<T extends AReferenceableEntity<T>> implements Serializable {

    private Class<T> targetClass;
    private String objectId;
    private String label;


    public DocumentLink() {
        //
    }

    @SuppressWarnings("unchecked")
    public DocumentLink(T object) {
        this((Class<T>) object.getClass(), object.getId(), object.getLabel());
    }

    public DocumentLink(Class<T> targetClass, String objectId, String label) {
        this.targetClass = targetClass;
        this.objectId = objectId;
        this.label = label;
    }

    public Class<T> getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(Class<T> targetClass) {
        this.targetClass = targetClass;
    }

    public String getObjectId() {
        return this.objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return String.format("%s [%s@%s]", this.label, this.objectId, this.targetClass.getSimpleName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.objectId == null) ? 0 : this.objectId.hashCode());
        result = (prime * result) + ((this.targetClass == null) ? 0 : this.targetClass.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DocumentLink)) {
            return false;
        }
        DocumentLink other = (DocumentLink) obj;
        if (this.objectId == null) {
            if (other.objectId != null) {
                return false;
            }
        } else if (!this.objectId.equals(other.objectId)) {
            return false;
        }
        if (this.targetClass == null) {
            if (other.targetClass != null) {
                return false;
            }
        } else if (!this.targetClass.equals(other.targetClass)) {
            return false;
        }
        return true;
    }

}
