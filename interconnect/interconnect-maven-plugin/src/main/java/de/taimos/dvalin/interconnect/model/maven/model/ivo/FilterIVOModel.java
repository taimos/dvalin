package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.maven.GeneratorHelper;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOFilterImports;
import de.taimos.dvalin.interconnect.model.metamodel.ContentDef;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.ImplementsDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import org.apache.maven.plugin.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author psigloch
 */
public class FilterIVOModel extends TemplateIVOModel {

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
        return super.getTargetFolder() + "/requests";
    }

    @Override
    public Map<String, String> generateClazzWithTemplates() {
        if(Boolean.TRUE.equals(this.definition.getInterfaceOnly())) {
            return null;
        }
        if((this.definition.getRemovalDate() == null) || this.definition.getRemovalDate().isEmpty() || GeneratorHelper.keepGeneratedFiles(this.definition.getRemovalDate())) {
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

    protected void beforeChildHandling() {
        this.definition.getChildren().addAll(new FilterMemberDef());
    }

    @Override
    protected void handleMemberAdditionaly(Object member) {
        super.handleMemberAdditionaly(member);
        if(member instanceof MemberDef) {
            FilterMemberDef.handleMember((MemberDef) member, this.filterableMemberDefs);
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
        if(this.definition.getCompatibleBaseVersion() != null) {
            builder.append(", ");
            builder.append(this.definition.getIVOClazzName(true));
        }
        for(ImplementsDef i : this.implementsDef) {
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

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.definition.getParentClazzName(false);
    }

    @Override
    public String getParentInterfaceName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.definition.getParentClazzName(true);
    }
}
