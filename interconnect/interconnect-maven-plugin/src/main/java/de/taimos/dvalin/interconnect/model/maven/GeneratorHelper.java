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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Properties;

/**
 * @author psigloch
 */
public class GeneratorHelper {

    /**
     * @param log       the logger
     * @param model     the generator model
     * @param targetDir the target path for generation
     * @throws MojoExecutionException on errors
     */
    public static void writeFile(Log log, GeneratorModel<?, ?> model, String targetDir) throws MojoExecutionException {
        if(model.generateClazzWithTemplates() == null || model.generateClazzWithTemplates().size() < 1) {
            return;
        }
        for(Map.Entry<String, String> templateEntry : model.generateClazzWithTemplates().entrySet()) {
            Template template;
            try {
                template = Velocity.getTemplate(templateEntry.getValue(), "UTF-8");
            } catch(Exception e) {
                log.error("Failed to retrieve Template " + templateEntry.getValue(), e);
                throw new MojoExecutionException("Failed to retrieve Template " + templateEntry.getValue(), e);
            }
            try {
                File pckDir = new File(targetDir + model.getTargetFolder());
                log.info("Writing to Folder " + pckDir.getAbsolutePath());
                if(!pckDir.exists()) {
                    pckDir.mkdirs();
                }
                try(OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(pckDir.getAbsolutePath() + File.separator + templateEntry.getKey() + ".java"), "UTF-8")) {
                    log.info("Creating file " + pckDir.getAbsolutePath() + File.separator + templateEntry.getKey() + ".java");
                    VelocityContext context = new VelocityContext();
                    context.put("model", model);
                    context.put("tool", new ModelTools());
                    context.put("clazzName", templateEntry.getKey());
                    template.merge(context, fw);
                }
            } catch(IOException e) {
                log.error("Failed to write the generated file " + templateEntry.getKey(), e);
                throw new MojoExecutionException("Failed to write the generated file " + templateEntry.getKey(), e);
            }
        }
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
     * @param log   the logger
     * @param f     the file to parse
     * @param <T>   the generator definition
     * @return the parsed IGeneratorDefinition
     * @throws MojoExecutionException on error
     */
    @SuppressWarnings("unchecked")
    public static <T extends IGeneratorDefinition> T parseXML(Class<T> clazz, Log log, File f) throws MojoExecutionException {
        try {
            JAXBContext jcontext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jcontext.createUnmarshaller();
            unmarshaller.setEventHandler(validationEvent -> false);
            return (T) unmarshaller.unmarshal(f);
        } catch(Exception e) {
            throw new MojoExecutionException("Failed to read input file " + f.getAbsolutePath(), e);
        }
    }

}
