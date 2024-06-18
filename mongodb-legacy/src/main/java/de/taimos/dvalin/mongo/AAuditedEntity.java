package de.taimos.dvalin.mongo;

/*-
 * #%L
 * MongoDB support for dvalin
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
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

/**
 * Copyright 2015 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 *
 */
public abstract class AAuditedEntity extends AEntity {
	
	private Integer version;
    private DateTime lastChange;
    private String lastChangeUser;
    
    public Integer getVersion() {
        return this.version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public DateTime getLastChange() {
        return this.lastChange;
    }
    
    public void setLastChange(DateTime lastChange) {
        this.lastChange = lastChange;
    }
    
    public String getLastChangeUser() {
        return this.lastChangeUser;
    }
    
    public void setLastChangeUser(String lastChangeUser) {
        this.lastChangeUser = lastChangeUser;
    }
    
}
