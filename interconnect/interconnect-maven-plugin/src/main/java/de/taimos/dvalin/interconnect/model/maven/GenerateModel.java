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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.sonatype.plexus.build.incremental.BuildContext;

import de.taimos.dvalin.interconnect.model.metamodel.IVODef;

/**
 * Interconnect IVO generator
 */
@Mojo(name = "generateModel", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateModel extends AbstractMojo {

	private static final String IVO_TEMPLATE = "ivotemplate.vm";
	private static final String IVO_ID_TEMPLATE = "ivoIdTemplate.vm";
	private static final String IVO_INTERFACE_ID_TEMPLATE = "ivoInterfaceIdTemplate.vm";
	private static final String IVO_OBJECT_TEMPLATE = "ivoObjectTemplate.vm";
	private static final String IVO_INTERFACE_OBJECT_TEMPLATE = "ivoInterfaceObjectTemplate.vm";
	private static final String IVO_INTERFACE_TEMPLATE = "ivointerfacetemplate.vm";
	private static final String TARGET_DIR = "/generated-sources/model/";
	private static final String IVO_INTERFACE_FILTER_TEMPLATE = "ivoInterfaceFilterTemplate.vm";
	private static final String IVO_FILTER_TEMPLATE = "ivoFilterTemplate.vm";

	@Component
	private BuildContext buildContext;

	@Parameter(required = true, property = "project.build.directory")
	private String outputDirectory;

	@Parameter(required = true)
	private File[] defdirs;

	@Parameter(required = true, property = "project", readonly = true)
	private MavenProject project;


	@Override
	public void execute() throws MojoExecutionException {
		try {
			for (File f : this.defdirs) {
				this.processDirectory(f);
			}
			File path = new File(this.outputDirectory + GenerateModel.TARGET_DIR);
			this.project.addCompileSourceRoot(path.getAbsolutePath());
			this.buildContext.refresh(path);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MojoExecutionException("Failed...", e);
		}
	}

	private void processDirectory(File f) throws MojoExecutionException {
		this.getLog().info("Processing Directory: " + f.getAbsolutePath());
		File[] dirs = f.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		if (dirs != null) {
			for (File file : dirs) {
				this.processDirectory(file);
			}
		}
		File[] defFiles = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		for (File defFile : defFiles) {
			this.getLog().info("Generating files for IVO in " + defFile.getAbsolutePath());
			this.processFile(defFile);
		}
	}

	private boolean notToBeRemoved(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date removalDate;
		try {
			removalDate = format.parse(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Failed to parse the removal date - should be yyyy/MM/dd, is " + dateString);
		}
		return removalDate.compareTo(Calendar.getInstance().getTime()) > 0;
	}

	private void processFile(File f) throws MojoExecutionException {
		IVODef ivod = GeneratorHelper.parseXML(IVODef.class, this.getLog(), f);
		Velocity.init(GeneratorHelper.getDefaultProps());
		VelocityContext metacontext = new VelocityContext();
		metacontext.put("ivod", ivod);
		metacontext.put("mmh", new MetaModelHelper(ivod));

		GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_INTERFACE_TEMPLATE, ivod.getPkgName(), "I" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);

		if ((ivod.getRemovalDate() == null) || ivod.getRemovalDate().isEmpty() || this.notToBeRemoved(ivod.getRemovalDate())) {
			if (!Boolean.TRUE.equals(ivod.getInterfaceOnly())) {
				GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_TEMPLATE, ivod.getPkgName(), ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
				this.generateFindById(ivod, metacontext);
				this.generateCreate(ivod, metacontext);
				this.generateDelete(ivod, metacontext);
				this.generateUpdate(ivod, metacontext);
				this.generateFilter(ivod, metacontext);
				this.generateFindByIDAudited(ivod, metacontext);
			}

		} else {
			this.getLog().info("IVO " + ivod.getName() + "_v" + ivod.getVersion() + " is beyond removal date, only the interface is generated.");
		}
	}

	/**
	 * @param ivod
	 * @param metacontext
	 * @throws MojoExecutionException
	 */
	private void generateFindById(IVODef ivod, VelocityContext metacontext) throws MojoExecutionException {
		if (Boolean.TRUE.equals(ivod.getGenerateFindById())) {
			((MetaModelHelper) metacontext.get("mmh")).setType(FileType.FINDBY);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_INTERFACE_ID_TEMPLATE, ivod.getPkgName() + "/requests", "IFind" + ivod.getName() + "ByIdIVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_ID_TEMPLATE, ivod.getPkgName() + "/requests", "Find" + ivod.getName() + "ByIdIVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
		}
	}

	/**
	 * @param ivod
	 * @param metacontext
	 * @throws MojoExecutionException
	 */
	private void generateCreate(IVODef ivod, VelocityContext metacontext) throws MojoExecutionException {
		if (Boolean.TRUE.equals(ivod.getGenerateCreate())) {
			((MetaModelHelper) metacontext.get("mmh")).setType(FileType.CREATE);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_INTERFACE_OBJECT_TEMPLATE, ivod.getPkgName() + "/requests", "ICreate" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_OBJECT_TEMPLATE, ivod.getPkgName() + "/requests", "Create" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
		}
	}

	/**
	 * @param ivod
	 * @param metacontext
	 * @throws MojoExecutionException
	 */
	private void generateUpdate(IVODef ivod, VelocityContext metacontext) throws MojoExecutionException {
		if (Boolean.TRUE.equals(ivod.getGenerateUpdate())) {
			((MetaModelHelper) metacontext.get("mmh")).setType(FileType.UPDATE);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_INTERFACE_OBJECT_TEMPLATE, ivod.getPkgName() + "/requests", "IUpdate" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_OBJECT_TEMPLATE, ivod.getPkgName() + "/requests", "Update" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
		}
	}

	/**
	 * @param ivod
	 * @param metacontext
	 * @throws MojoExecutionException
	 */
	private void generateDelete(IVODef ivod, VelocityContext metacontext) throws MojoExecutionException {
		if (Boolean.TRUE.equals(ivod.getGenerateDelete())) {
			((MetaModelHelper) metacontext.get("mmh")).setType(FileType.DELETE);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_INTERFACE_ID_TEMPLATE, ivod.getPkgName() + "/requests", "IDelete" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_ID_TEMPLATE, ivod.getPkgName() + "/requests", "Delete" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
		}
	}

	/**
	 * @param ivod
	 * @param metacontext
	 * @throws MojoExecutionException
	 */
	private void generateFilter(IVODef ivod, VelocityContext metacontext) throws MojoExecutionException {
		if (Boolean.TRUE.equals(ivod.getGenerateFilter())) {
			((MetaModelHelper) metacontext.get("mmh")).setType(FileType.FILTER);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_INTERFACE_FILTER_TEMPLATE, ivod.getPkgName() + "/requests", "IFind" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_FILTER_TEMPLATE, ivod.getPkgName() + "/requests", "Find" + ivod.getName() + "IVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
		}
	}

	private void generateFindByIDAudited(IVODef ivod, VelocityContext metacontext) throws MojoExecutionException {
		if (Boolean.TRUE.equals(ivod.getAuditing()) && Boolean.TRUE.equals(ivod.getGenerateFindById())) {
			((MetaModelHelper) metacontext.get("mmh")).setType(FileType.AUDITING);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_INTERFACE_ID_TEMPLATE, ivod.getPkgName() + "/requests", "IFind" + ivod.getName() + "ByIdAuditedIVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
			GeneratorHelper.writeFile(this.getLog(), metacontext, GenerateModel.IVO_ID_TEMPLATE, ivod.getPkgName() + "/requests", "Find" + ivod.getName() + "ByIdAuditedIVO_v" + ivod.getVersion(), this.outputDirectory, GenerateModel.TARGET_DIR);
		}
	}
}
