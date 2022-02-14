package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.imports.ivo.IVOImports;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.defs.AuditedMemberDef;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.defs.PageableMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.CollectionMemberDef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.MemberDef;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author psigloch
 */
public class IVOModel extends AbstractIVOModel {
    private static final String IVO = "ivo/ivo.vm";

    /**
     * @param definition               the definition
     * @param logger                   the logger
     * @param additionalMemberHandlers the additional member handlers
     */
    public IVOModel(IVODef definition, Log logger, IAdditionalMemberHandler... additionalMemberHandlers) {
        super(additionalMemberHandlers);
        this.init(definition, new IVOImports(), logger);
    }

    @Override
    public Collection<GenerationContext> getGenerationContexts() {
        if (Boolean.TRUE.equals(this.definition.getInterfaceOnly())) {
            return Collections.emptySet();
        }
        Set<GenerationContext> result = new HashSet<>();
        if (this.generateFile()) {
            result.add(new GenerationContext(IVOModel.IVO, this.getClazzName(), false));
        } else if (this.getLogger() != null) {
            this.getLogger().info(this.getClazzName() + " is beyond removal date, only the interface is generated.");
        }
        return result;
    }

    @Override
    protected void beforeChildHandling() {
        super.beforeChildHandling();
        if (Boolean.TRUE.equals(this.definition.getAuditing())) {
            this.addChildren(new AuditedMemberDef());
        }
        if (Boolean.TRUE.equals(this.definition.getPageable())) {
            this.addChildren(new PageableMemberDef());
        }
    }

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : this.definition.getParentName() + "IVO_v" + this.definition.getParentVersion();
    }

    @Override
    public String getParentInterfaceName() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getSimpleName() : "I" + this.getParentClazzName();
    }

    @Override
    public String getParentClazzPath() {
        return this.definition.getParentName() == null ? AbstractIVO.class.getCanonicalName() : this.definition.getParentPkgName() + "." + this.getParentClazzName();
    }

    @Override
    public boolean hasParentClazz() {
        boolean res = this.definition.getParentName() != null;
        if (res) {
            res = !this.definition.getParentName().trim().isEmpty();
        }
        return res;
    }

    @Override
    public List<MemberDef> getAllFields() {
        return super.getAllFields().stream().filter(memberDef -> Boolean.FALSE.equals(memberDef.getFilterOnly())).collect(Collectors.toList());
    }

    @Override
    public List<CollectionMemberDef> getCollectionFields() {
        return super.getCollectionFields().stream().filter(memberDef -> Boolean.FALSE.equals(memberDef.getFilterOnly())).collect(Collectors.toList());
    }

    @Override
    public List<MemberDef> getNoCollectionFields() {
        return super.getNoCollectionFields().stream().filter(memberDef -> Boolean.FALSE.equals(memberDef.getFilterOnly())).collect(Collectors.toList());
    }

    /**
     * velocity use
     *
     * @return provides ivo end addition path, relative to resources/ivo
     */
    public Collection<String> getIVOEndAddition() {
        return this.additionalMemberHandlers.stream().map(IAdditionalMemberHandler::getIVOTemplateAddition).filter(Objects::nonNull).filter(amh -> !amh.trim().isEmpty()).collect(Collectors.toSet());
    }

    /**
     * @return whether ivo his pageable or not
     */
    public boolean isPageable() {
        return this.definition.getPageable();
    }
}
