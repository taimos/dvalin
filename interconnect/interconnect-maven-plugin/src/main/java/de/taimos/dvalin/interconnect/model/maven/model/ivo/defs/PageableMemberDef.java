package de.taimos.dvalin.interconnect.model.maven.model.ivo.defs;

import de.taimos.dvalin.interconnect.model.ivo.Direction;

/**
 * Copyright 2022 <br>
 * <br>
 *
 * @author psigloch
 */
public class PageableMemberDef extends AMemberDef {

    private static final long serialVersionUID = -3548615757353384757L;

    /**
     * constructor add default init;
     */
    public PageableMemberDef() {
        this.add(this.createIntegerMemberDef("limit", "the maximum result size"));
        this.add(this.createIntegerMemberDef("offset", "the offset of the first result"));
        this.add(this.createStringMemberDef("sortBy", "provide this to enable a correct sorted paging of your lists. Use sortDirection to provide information about sort direction"));
        this.add(this.createEnumMemberDef(Direction.class, "sortDirection", "provide this to enable a correct sorted paging of your lists. Use sortBy to provide information about the property to sort by"));
    }
}
