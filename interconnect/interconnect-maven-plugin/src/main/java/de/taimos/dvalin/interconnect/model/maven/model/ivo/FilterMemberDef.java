package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.Direction;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.FilterableType;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.DateMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.EnumMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IntegerMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.StringMemberDef;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author psigloch
 */
public class FilterMemberDef extends ArrayList<Object> {

    private static final long serialVersionUID = -6584896154646679708L;

    /**
     * constructor with default init;
     */
    public FilterMemberDef() {
        this.add(this.createIntegerMemberDef("limit", "the maximum result size", FilterableType.single));
        this.add(this.createIntegerMemberDef("offset", "the offset of the first result", FilterableType.single));
        this.add(this.createStringMemberDef());
        this.add(this.createEnumMemberDef());
    }

    /**
     * @param o                    the member def to check
     * @param filterableMemberDefs the list of member defs to add to
     */
    public static void handleMember(MemberDef o, List<Object> filterableMemberDefs) {
        if(!o.isAFilterMember()) {
            return;
        }
        if(o instanceof DateMemberDef) {
            try {
                DateMemberDef min = (DateMemberDef) BeanUtils.cloneBean(o);
                DateMemberDef max = (DateMemberDef) BeanUtils.cloneBean(o);
                min.setName(min.getName() + "Min");
                max.setName(max.getName() + "Max");
                filterableMemberDefs.add(min);
                filterableMemberDefs.add(max);
            } catch(IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                filterableMemberDefs.add(o);
            }
        } else {
            filterableMemberDefs.add(o);
        }
    }

    private IntegerMemberDef createIntegerMemberDef(String name, String comment, FilterableType filterableType) {
        IntegerMemberDef imd = new IntegerMemberDef();
        imd.setComment(comment);
        imd.setJavaTransientFlag(Boolean.FALSE);
        imd.setJsonTransientFlag(Boolean.FALSE);
        imd.setName(name);
        imd.setOrderTransient(Boolean.FALSE);
        imd.setRequired(true);
        imd.setFilterable(filterableType);
        return imd;
    }


    private StringMemberDef createStringMemberDef() {
        StringMemberDef smd = new StringMemberDef();
        smd.setComment("provide this to enable a correct sorted paging of your lists. Use {@link #withSortDirection(Direction)} to provide information about sort direction");
        smd.setJavaTransientFlag(Boolean.FALSE);
        smd.setJsonTransientFlag(Boolean.FALSE);
        smd.setName("sortBy");
        smd.setOrderTransient(Boolean.FALSE);
        smd.setRequired(false);
        smd.setFilterable(FilterableType.single);
        return smd;
    }

    private EnumMemberDef createEnumMemberDef() {
        EnumMemberDef emd = new EnumMemberDef();
        emd.setComment("provide this to enable a correct sorted paging of your lists. Use {@link #withSortBy(String)} to provide information about the property to sort by");
        emd.setJavaTransientFlag(Boolean.FALSE);
        emd.setJsonTransientFlag(Boolean.FALSE);
        emd.setName("sortDirection");
        emd.setOrderTransient(Boolean.FALSE);
        emd.setRequired(false);
        emd.setClazz(Direction.class.getSimpleName());
        emd.setPkgName(Direction.class.getPackage().getName());
        emd.setFilterable(FilterableType.single);
        return emd;
    }
}
