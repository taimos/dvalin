package de.taimos.dvalin.interconnect.model.maven.model.ivo.defs;

import de.taimos.dvalin.interconnect.model.metamodel.memberdef.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.EnumMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.FilterableType;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IntegerMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.StringMemberDef;

import java.util.ArrayList;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public abstract class AMemberDef extends ArrayList<Object> {
    private static final long serialVersionUID = -9151726396715235948L;

    protected IntegerMemberDef createIntegerMemberDef(String name, String comment) {
        IntegerMemberDef imd = new IntegerMemberDef();
        imd.setComment(comment);
        imd.setName(name);
        return this.defaultFill(imd);
    }

    protected DateMemberDef createDateTimeMemberDef(String name, String comment) {
        DateMemberDef imd = new DateMemberDef();
        imd.setComment(comment);
        imd.setName(name);
        return this.defaultFill(imd);
    }


    protected StringMemberDef createStringMemberDef(String name, String comment) {
        StringMemberDef smd = new StringMemberDef();
        smd.setComment(comment);
        smd.setName(name);
        return this.defaultFill(smd);
    }

    protected EnumMemberDef createEnumMemberDef(Class<? extends Enum<?>> clazz, String name, String comment) {
        EnumMemberDef emd = new EnumMemberDef();
        emd.setComment(comment);
        emd.setName(name);
        emd.setClazz(clazz.getSimpleName());
        emd.setPkgName(clazz.getPackage().getName());
        return this.defaultFill(emd);
    }

    private <M extends MemberDef> M defaultFill(M member) {
        member.setJavaTransientFlag(Boolean.FALSE);
        member.setJsonTransientFlag(Boolean.FALSE);
        member.setOrderTransient(Boolean.FALSE);
        member.setRequired(false);
        member.setFilterable(FilterableType.single);
        return member;
    }
}
