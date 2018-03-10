package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;

/**
 * @author psigloch
 */
public class IVOImports extends BaseIVOImports {


    private static final long serialVersionUID = -8005210350264896152L;

    @Override
    public void initDefaults() {
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.withNullable();
        this.withNonnull();
        this.with(IVOBuilder.class);
    }

    @Override
    public void initFromDefintion(IVODef ivoDefinition, AbstractInterconnectModel model) {
        super.initFromDefintion(ivoDefinition, model);
        if(!model.hasParentClazz()) {
            this.with(AbstractIVO.class);
        }
    }
}
