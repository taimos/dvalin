package de.taimos.dvalin.interconnect.model.maven.model;

import de.taimos.dvalin.interconnect.model.maven.imports.Imports;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author psigloch
 */
public abstract class GeneratorModel<T extends IGeneratorDefinition, K extends Imports<T>> {

    private static final String DEFAULT_TARGET_DIR = "/generated-sources/model/";

    protected K imports;
    protected T definition;
    private Log logger;

    /**
     * @return a map conting pairs of clazznames (key) and velocity template (value) to generate
     */
    public abstract Map<String, String> generateClazzWithTemplates();

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
        if(ivoDefinition == null || imports == null) {
            throw new RuntimeException("Failed. Generator was not correctly initialized");
        }
        this.logger = logger;
        this.definition = ivoDefinition;
        this.imports = imports;
        this.imports.initDefaults();

        this.prepareChildren();
        this.beforeChildHandling();
        for(Object child : this.definition.getChildren()) {
            this.handleChild(child);
        }
        this.afterChildHandling();
    }


    protected void beforeChildHandling() {
        //override if needed
    }

    protected void afterChildHandling() {
        //override if needed
    }

    private void prepareChildren() {
        if(this.definition.getChildren() == null) {
            this.definition.setChildren(new ArrayList<>());
        }
    }

    /**
     * @return the logger
     */
    protected Log getLogger() {
        return this.logger;
    }
}
