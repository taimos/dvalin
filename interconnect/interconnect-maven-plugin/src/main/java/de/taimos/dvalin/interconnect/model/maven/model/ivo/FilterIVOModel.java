package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IIdentity;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.util.IIVOAuditing;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOFilterImports;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ContentDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.IVOMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author psigloch
 */
public class FilterIVOModel extends AbstractIVOModel {

    private static final String FIND_BY = "ivo/findBy.vm";
    private static final String FIND_BY_INTERFACE = "ivo/findByInterface.vm";
    private static final String FIND_INTERFACE = "ivo/findInterface.vm";
    private static final String FIND = "ivo/find.vm";

    private final List<Object> filterableMemberDefs = new ArrayList<>();

    /**
     * @param definition the definition
     * @param logger     the logger
     */
    public FilterIVOModel(IVODef definition, Log logger) {
        this.init(definition, new IVOFilterImports(), logger);
    }

    @Override
    public String getTargetFolder() {
        return super.getTargetFolder() + File.separator + "requests";
    }

    @Override
    public Map<String, String> generateClazzWithTemplates() {
        if(Boolean.TRUE.equals(this.definition.getInterfaceOnly())) {
            return null;
        }
        if(this.genereateFile()) {
            Map<String, String> result = new HashMap<>();
            if(this.definition.getGenerateFindById()) {
                result.put(this.getFileName("ByIdIVO_v", true), FilterIVOModel.FIND_BY_INTERFACE);
                result.put(this.getFileName("ByIdIVO_v", false), FilterIVOModel.FIND_BY);

                if(this.definition.getAuditing()) {
                    result.put(this.getFileName("ByIdAuditedIVO_v", true), FilterIVOModel.FIND_BY_INTERFACE);
                    result.put(this.getFileName("ByIdAuditedIVO_v", false), FilterIVOModel.FIND_BY);
                }
            }

            if(this.definition.getGenerateFilter()) {
                result.put(this.getFileName("IVO_v", true), FilterIVOModel.FIND_INTERFACE);
                result.put(this.getFileName("IVO_v", false), FilterIVOModel.FIND);
            }

            return result;
        }

        return null;
    }

    private String getFileName(String subString, boolean isInterface) {
        return (isInterface ? "I" : "") + "Find" + this.definition.getName() + subString + this.definition.getVersion();
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        this.definition.getChildren().addAll(new FilterMemberDef());
    }

    @Override
    protected void handleMemberAdditionaly(Object member) {
        super.handleMemberAdditionaly(member);
        if(member instanceof MemberDef) {
            FilterMemberDef.handleMember((MemberDef) member, this.filterableMemberDefs);
        }
        if(member instanceof IVOMemberDef) {
            this.imports.with(((IVOMemberDef) member).getIVOPath(false));
            this.imports.with(((IVOMemberDef) member).getIVOPath(true));
        }
    }

    @Override
    protected void handleContentMembers(ContentDef content) {
        super.handleContentMembers(content);
        switch(content.getType()) {
            case IVO:
                if(content.getIvoName() != null) {
                    this.imports.add(content.getPath(true));
                }
                break;
            default:
                break;
        }
    }

    public Collection<Object> getFilterableFields() {
        return this.filterableMemberDefs;
    }

    public String getInterfaceImplements(boolean multiFilter) {
        StringBuilder builder = new StringBuilder();
        for(ImplementsDef i : this.implementsDef) {
            if(i.getName().equalsIgnoreCase(IIdentity.class.getSimpleName())) {
                continue;
            }
            if(i.getName().equalsIgnoreCase(IIVOAuditing.class.getSimpleName())) {
                continue;
            }
            if(i.getName().equalsIgnoreCase("I" + super.getParentClazzName())) {
                continue;
            }
            builder.append(", ");
            builder.append(i.getName());
        }

        if(multiFilter) {
            builder.append(", ");
            builder.append(IPageable.class.getSimpleName());
        }

        if(builder.length() < 1) {
            return "";
        }

        return "extends " + builder.substring(2);
    }

    /**
     * @return wheteher the ivo has a parent object or not
     */
    @Override
    public boolean hasParentClazz() {
        return false;
    }

    @Override
    public String getParentClazzName() {
        return AbstractIVO.class.getSimpleName();
    }

    @Override
    public String getParentInterfaceName() {
        return AbstractIVO.class.getSimpleName();
    }
}
