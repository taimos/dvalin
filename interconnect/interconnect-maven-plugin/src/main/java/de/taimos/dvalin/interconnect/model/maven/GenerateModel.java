package de.taimos.dvalin.interconnect.model.maven;

/*
 * #%L
 * Dvalin interconnect maven plugin for source generation
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import de.taimos.dvalin.interconnect.model.maven.model.event.EventModel;
import de.taimos.dvalin.interconnect.model.maven.model.event.InterfaceEventModel;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.EditIVOModel;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.FilterIVOModel;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.IVOModel;
import de.taimos.dvalin.interconnect.model.maven.model.ivo.InterfaceIVOModel;
import de.taimos.dvalin.interconnect.model.metamodel.defs.EventDef;
import de.taimos.dvalin.interconnect.model.metamodel.defs.IVODef;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.app.Velocity;
import org.sonatype.plexus.build.incremental.BuildContext;

import java.io.File;

/**
 * Interconnect IVO generator
 */
@Mojo(name = "generateModel", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateModel extends AbstractMojo {


    private static final String TARGET_DIR_EVENT = "/generated-sources/model/event/";

    @Component
    private BuildContext buildContext;

    @Parameter(required = true, property = "project.build.directory")
    private String outputDirectory;

    @Parameter()
    @Deprecated
    private File[] defdirs;

    @Parameter()
    private File[] ivoPaths;

    @Parameter()
    private File[] eventPaths;

    @Parameter(required = true, property = "project", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException {
        Velocity.init(GeneratorHelper.getVelocityDefaultProps());

        //handle ivo generation
        this.execute(this.ivoPaths, ModelType.IVO);
        //fallback support for old configuration
        this.execute(this.defdirs, ModelType.IVO);

        //handle event generation
        this.execute(this.eventPaths, ModelType.EVENT);
    }

    private void execute(File[] dir, ModelType type) throws MojoExecutionException {
        try {
            //support for old configuration
            if(dir != null && dir.length > 0) {
                for(File f : dir) {
                    this.processDirectory(f, type);
                }
                File path = new File(this.getOutputDirectory());
                this.project.addCompileSourceRoot(path.getAbsolutePath());
                this.buildContext.refresh(path);
            }
        } catch(Exception e) {
            throw new MojoExecutionException("Failed...", e);
        }
    }

    private void processDirectory(File f, ModelType type) throws MojoExecutionException {
        this.getLog().info("Processing Directory: " + f.getAbsolutePath());
        File[] dirs = f.listFiles(File::isDirectory);
        if(dirs != null) {
            for(File file : dirs) {
                this.processDirectory(file, type);
            }
        }
        File[] defFiles = f.listFiles((dir, name) -> name.endsWith(".xml"));
        if(defFiles != null) {
            for(File defFile : defFiles) {
                switch(type) {
                    case IVO:
                        this.getLog().info("Generating files for IVO in " + defFile.getAbsolutePath());
                        try {
                            this.processFileAsIVO(defFile);
                        } catch(MojoExecutionException e) {
                            if(e.getCause().getMessage().contains("event")) {
                                this.getLog().warn("An event file was found in the ivo directory. Please fix this.");
                                this.processFileAsEvent(defFile);
                            } else {
                                this.getLog().error("Failed to read input file " + f.getAbsolutePath(), e);
                                throw e;
                            }
                        }
                        break;
                    case EVENT:
                        this.getLog().info("Generating files for Event in " + defFile.getAbsolutePath());
                        try {
                            this.processFileAsEvent(defFile);
                        } catch(MojoExecutionException e) {
                            if(e.getCause().getMessage().contains("ivo")) {
                                this.getLog().warn("An ivo file was found in the ivo directory. Please fix this.");
                                this.processFileAsIVO(defFile);
                            } else {
                                this.getLog().error("Failed to read input file " + f.getAbsolutePath(), e);
                                throw e;
                            }
                        }
                        break;
                }
            }
        }
    }

    protected void processFileAsIVO(File f) throws MojoExecutionException {
        IVODef ivod = GeneratorHelper.parseXML(IVODef.class, this.getLog(), f);
        GeneratorHelper.writeFile(this.getLog(), new InterfaceIVOModel(ivod, this.getLog()), this.getOutputDirectory());
        GeneratorHelper.writeFile(this.getLog(), new IVOModel(ivod, this.getLog()), this.getOutputDirectory());
        GeneratorHelper.writeFile(this.getLog(), new FilterIVOModel(ivod, this.getLog()), this.getOutputDirectory());
        GeneratorHelper.writeFile(this.getLog(), new EditIVOModel(ivod, this.getLog()), this.getOutputDirectory());
    }

    protected String getOutputDirectory() {
        return this.outputDirectory;
    }

    protected void processFileAsEvent(File f) throws MojoExecutionException {
        EventDef eventd = GeneratorHelper.parseXML(EventDef.class, this.getLog(), f);
        GeneratorHelper.writeFile(this.getLog(), new InterfaceEventModel(eventd, this.getLog()), this.getOutputDirectory());
        GeneratorHelper.writeFile(this.getLog(), new EventModel(eventd, this.getLog()), this.getOutputDirectory());
    }


}

