package de.taimos.dvalin.interconnect.model.maven;

/*
 * #%L
 * Dvalin interconnect maven plugin for source generation
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance add the License.
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

import de.taimos.dvalin.interconnect.model.maven.model.GeneratorModel;
import de.taimos.dvalin.interconnect.model.maven.model.ModelTools;
import de.taimos.dvalin.interconnect.model.metamodel.IGeneratorDefinition;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.runtime.RuntimeConstants;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author psigloch
 */
public class GeneratorHelper {

    /**
     * @param model     the generator model
     * @param targetDir the target path for generation
     * @throws MojoExecutionException on errors
     */
    public static void writeFile(GeneratorModel<?, ?> model, String targetDir) throws MojoExecutionException {
        if (model.getGenerationContexts() == null || model.getGenerationContexts().isEmpty()) {
            return;
        }
        for (GenerationContext generationContext : model.getGenerationContexts()) {
            generationContext.setTemplate(GeneratorHelper.prepareTemplate(model.getLogger(), generationContext));
            generationContext.setTargetDir(targetDir + model.getTargetFolder());
            GeneratorHelper.createFileWithTemplate(model, generationContext);
        }
    }

    private static void createFileWithTemplate(GeneratorModel<?, ?> model, GenerationContext generationContext) throws MojoExecutionException {
        try {
            try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(generationContext.getTargetPath()), StandardCharsets.UTF_8)) {
                model.getLogger().info("Creating file " + generationContext.getTargetPath());
                VelocityContext context = new VelocityContext();
                context.put("model", model);
                context.put("tool", new ModelTools());
                context.put("clazzName", generationContext.getTargetFileName());
                generationContext.getTemplate().merge(context, fw);
            }
        } catch (IOException e) {
            model.getLogger().error("Failed to write the generated file " + generationContext.getTargetFileName(), e);
            throw new MojoExecutionException("Failed to write the generated file " + generationContext.getTargetFileName(), e);
        }
    }

    private static Template prepareTemplate(Log log, GenerationContext generationContext) throws MojoExecutionException {
        Template template;
        try {
            template = Velocity.getTemplate(generationContext.getTemplatePath(), generationContext.getTemplateEncoding());
        } catch (Exception e) {
            log.error("Failed to retrieve Template " + generationContext.getTemplatePath(), e);
            throw new MojoExecutionException("Failed to retrieve Template " + generationContext.getTemplatePath(), e);
        }
        return template;
    }

    /**
     * @return the default properties for velicity
     */
    public static Properties getVelocityDefaultProps() {
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        props.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
        return props;
    }

    /**
     * @param clazz the generator definiton clazz
     * @param f     the file to parse
     * @param <T>   the generator definition
     * @return the parsed IGeneratorDefinition
     * @throws MojoExecutionException on error
     */
    @SuppressWarnings("unchecked")
    public static <T extends IGeneratorDefinition> T parseXML(Class<T> clazz, File f) throws MojoExecutionException {
        try {
            JAXBContext jcontext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jcontext.createUnmarshaller();
            unmarshaller.setEventHandler(validationEvent -> false);
            return (T) unmarshaller.unmarshal(f);
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to read input file " + f.getAbsolutePath(), e);
        }
    }

}
