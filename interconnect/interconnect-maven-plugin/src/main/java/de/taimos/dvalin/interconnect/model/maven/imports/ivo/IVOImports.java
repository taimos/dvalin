package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IPageable;
import de.taimos.dvalin.interconnect.model.ivo.IPageableBuilder;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.AbstractIVOModel;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.IVOModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;

import java.util.Objects;

/**
 * @author psigloch
 */
public class IVOImports extends BaseIVOImports {


    private static final long serialVersionUID = -8005210350264896152L;

    @Override
    public void initDefaults() {
        super.initDefaults();
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.withNullable();
        this.withNonnull();
        this.add(IVOBuilder.class);
    }

    @Override
    public <K extends AbstractInterconnectModel<IVODef, ? extends Imports<IVODef>>> void initFromDefinition(IVODef ivoDefinition, K model) {
        super.initFromDefinition(ivoDefinition, model);
        if (!model.hasParentClazz()) {
            this.add(AbstractIVO.class);
        }
        if (model instanceof AbstractIVOModel) {
            if (((AbstractIVOModel) model).isIdentity() && !model.hasParentClazz()) {
                this.add(Objects.class);
            }
        }
        if (model instanceof IVOModel) {
            if (((IVOModel) model).isPageable()) {
                this.add(IPageable.class);
                this.add(IPageableBuilder.class);
            }
        }
    }
}
