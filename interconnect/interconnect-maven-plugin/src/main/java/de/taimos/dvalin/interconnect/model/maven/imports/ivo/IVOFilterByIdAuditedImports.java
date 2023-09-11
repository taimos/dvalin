package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.Direction;
import de.taimos.dvalin.interconnect.model.ivo.IPageableBuilder;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;

/**
 * @author psigloch
 */
public class IVOFilterByIdAuditedImports extends BaseIVOImports {

    private static final long serialVersionUID = -905784989070923147L;

    @Override
    public void initDefaults() {
        super.initDefaults();
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.add(Collections.class);
        this.add(Collection.class);
        this.add(AbstractIVO.class);
        this.add(IVOBuilder.class);
        this.add(Direction.class);
        this.add(IPageableBuilder.class);
        this.add(Nonnull.class);
        this.add(Nullable.class);
        this.withJsonIgnore();
    }

    @Override
    public <K extends AbstractInterconnectModel<IVODef, ? extends Imports<IVODef>>> void initFromDefinition(IVODef ivoDefinition, K model) {
        //do nothing
    }

}
