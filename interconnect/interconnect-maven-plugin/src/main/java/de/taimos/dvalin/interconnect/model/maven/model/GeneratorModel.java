package de.taimos.dvalin.interconnect.model.maven.model;

import de.taimos.dvalin.interconnect.model.maven.GenerationContext;
import de.taimos.dvalin.interconnect.model.maven.exceptions.IVOGenerationError;
import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author psigloch
 */
public abstract class GeneratorModel<T extends IGeneratorDefinition, K extends Imports<T>> {

    public static final String DEFAULT_TARGET_DIR = "/generated-sources/model/";

    protected K imports;
    protected T definition;
    private Log logger;
    protected List<Object> modifiableChildren = new ArrayList<>();

    /**
     * @return a map conting pairs of clazznames (key) and velocity template (value) to generate
     */
    public abstract Collection<GenerationContext> getGenerationContexts();

    protected abstract void handleChild(Object child);

    /**
     * Override if needed
     *
     * @return subdirectory path
     */
    public String getTargetFolder() {
        return GeneratorModel.DEFAULT_TARGET_DIR + this.definition.getPackageName().replace('.', File.separatorChar);
    }

    /**
     * @param ivoDefinition ivoDefinotion
     * @param imports       the imports
     * @param logger        the logger to use
     */
    public void init(T ivoDefinition, K imports, Log logger) {
        if (ivoDefinition == null || imports == null) {
            throw new IVOGenerationError("Failed. Generator was not correctly initialized");
        }
        this.logger = logger;
        this.definition = ivoDefinition;
        this.imports = imports;
        this.imports.initDefaults();

        if (this.definition.getChildren() != null) {
            this.modifiableChildren.addAll(this.definition.getChildren());
        }

        this.beforeChildHandling();
        for (Object child : new ArrayList<>(this.modifiableChildren)) {
            this.handleChild(child);
        }
    }


    protected void beforeChildHandling() {
        //override if needed
    }

    protected void addChild(Object child) {
        if (child != null) {
            this.modifiableChildren.add(child);
        }
    }

    protected void addChildren(Collection<Object> children) {
        if (children != null) {
            this.modifiableChildren.addAll(children);
        }
    }

    /**
     * @return the logger
     */
    public Log getLogger() {
        return this.logger;
    }
}
