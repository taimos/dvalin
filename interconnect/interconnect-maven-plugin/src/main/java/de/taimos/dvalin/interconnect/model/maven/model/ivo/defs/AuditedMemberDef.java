package de.taimos.dvalin.interconnect.model.maven.model.ivo.defs;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class AuditedMemberDef extends AMemberDef {

    private static final long serialVersionUID = -3548615757353384757L;

    /**
     * constructor add default init;
     */
    public AuditedMemberDef() {
        this.add(this.createIntegerMemberDef("version", "the value for version"));
        this.add(this.createDateTimeMemberDef("lastChange", "the last change date"));
        this.add(this.createStringMemberDef("lastChangeUser", "the last change user"));
    }
}
