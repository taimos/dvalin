package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.taimos.dvalin.interconnect.model.ivo.IIdentity;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.AbstractIVOModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;

/**
 * @author psigloch
 */
public class IVOInterfaceImports extends BaseIVOImports {

    private static final long serialVersionUID = -4856513469922204335L;

    @Override
    public void initDefaults() {
        this.withNullable();
        this.withNonnull();
        this.withJsonTypeInfo();
    }

    @Override
    public void initFromDefintion(IVODef ivoDefinition, AbstractInterconnectModel model) {
        super.initFromDefintion(ivoDefinition, model);
        if(model instanceof AbstractIVOModel) {
            if(((AbstractIVOModel) model).isIdentity()) {
                this.with(JsonIgnore.class);
            }

            if(model.hasParentClazz()) {
                this.with(model.getParentInterfacePath());
            }else {
                this.with(IVO.class);
            }

            if(((AbstractIVOModel) model).isIdentity() && !model.hasParentClazz()) {
                this.with(IIdentity.class);
            }
        }
    }
}
