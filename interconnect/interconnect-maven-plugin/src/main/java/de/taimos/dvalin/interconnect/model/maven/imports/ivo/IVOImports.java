package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.metamodel.IVODef;

/**
 * @author psigloch
 */
public class IVOImports extends BaseIVOImports{


    private static final long serialVersionUID = -8005210350264896152L;

    public void initDefaults() {
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.withNullable();
        this.withNunnull();
        this.with(IVOBuilder.class);
        this.with(IVO.class);
    }

    public void initFromDefintion(IVODef ivoDefinition) {
        this.withIVODefinition(ivoDefinition);
        if ((ivoDefinition.getParentName() == null)) {
            this.with(AbstractIVO.class.getCanonicalName());
        }
    }
}
