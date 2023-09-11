package de.taimos.dvalin.interconnect.model.maven.imports.ivo;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;
import de.taimos.dvalin.interconnect.model.ivo.IVOBuilder;
import de.taimos.dvalin.interconnect.model.ivo.util.IdWithVersion;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.maven.model.AbstractInterconnectModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author psigloch
 */
public class IVODeleteImports extends BaseIVOImports {

    private static final long serialVersionUID = -905784989070923147L;

    @Override
    public void initDefaults() {
        super.initDefaults();
        this.withJsonDeserialize();
        this.withJsonPOJOBuilder();
        this.add(Collections.class);
        this.add(Collection.class);
        this.add(List.class);
        this.add(AbstractIVO.class);
        this.add(ArrayList.class);
        this.add(IdWithVersion.class);
        this.add(Collectors.class);
        this.add(IVOBuilder.class);
        this.add(Nonnull.class);
    }

    @Override
    public <K extends AbstractInterconnectModel<IVODef, ? extends Imports<IVODef>>> void initFromDefinition(IVODef ivoDefinition, K model) {
        //do nothing
    }

}
