package de.taimos.dvalin.mongo;

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
