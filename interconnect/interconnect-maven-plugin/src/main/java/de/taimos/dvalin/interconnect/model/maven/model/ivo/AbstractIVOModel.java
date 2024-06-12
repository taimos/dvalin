package de.taimos.dvalin.interconnect.model.maven.model.ivo;

import de.taimos.dvalin.interconnect.model.maven.imports.ivo.BaseIVOImports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.maven.model.IAdditionalMemberHandler;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ILabelMember;
import de.taimos.dvalin.interconnect.model.metamodel.memberdef.ImplementsDef;

import java.util.ArrayList;
import java.util.List;

/**
 * implements most helper methods used within velocity templates for ivos
 *
 * @author psigloch
 */
public abstract class AbstractIVOModel extends AbstractInterconnectModel<IVODef, BaseIVOImports> {

    protected final List<ILabelMember> labelMember = new ArrayList<>();

    protected AbstractIVOModel(IAdditionalMemberHandler... additionalMemberHandlers) {
        super(additionalMemberHandlers);
    }

    @Override
    protected void handleChild(Object child) {
        if ((child instanceof ILabelMember) && Boolean.TRUE.equals(((ILabelMember) child).useAsLabel())) {
            this.labelMember.add((ILabelMember) child);
        }
        super.handleChild(child);
    }

    @Override
    public String getClazzPath() {
        return this.definition.getPackageName() + "." + this.getClazzName();
    }

    @Override
    public String getClazzName() {
        return this.definition.getName() + "IVO_v" + this.definition.getVersion();
    }

    @Override
    public String getParentClazzName() {
        return this.definition.getParentName() != null ? this.definition.getParentName() + "IVO_v" + this.definition.getParentVersion() : null;
    }

    @Override
    public String getParentClazzPath() {
        return this.definition.getParentPkgName() + "." + this.getParentClazzName();
    }

    @Override
    public String getInterfaceImplements() {
        StringBuilder builder = new StringBuilder();
        if (this.definition.getCompatibleBaseVersion() != null) {
            builder.append(", ");
            builder.append(this.getInterfaceClazzName(), 0, this.getInterfaceClazzName().length() - 1).append(this.definition.getCompatibleBaseVersion());
        }
        for (ImplementsDef i : this.implementsDef) {
            builder.append(", ");
            builder.append(i.getName());
        }
        if (builder.toString().trim().isEmpty()) {
            return "";
        }
        return "extends " + builder.substring(2);
    }

    /**
     * @return whether ivo is audited or not
     */
    public boolean isAudited() {
        return Boolean.TRUE.equals(this.definition.getAuditing());
    }

    /**
     * @return whether ivo has a identity field or not
     */
    public boolean isIdentity() {
        return this.definition.getIdentity();
    }

}
