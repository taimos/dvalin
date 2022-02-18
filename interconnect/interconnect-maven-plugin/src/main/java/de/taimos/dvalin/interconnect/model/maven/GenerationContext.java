package de.taimos.dvalin.interconnect.model.maven;

import org.apache.velocity.Template;

import java.io.File;

/**
 * Copyright 2022 Cinovo AG<br>
 * <br>
 *
 * @author psigloch
 */
public class GenerationContext {

    private final String templatePath;
    private final String targetFileName;
    private final boolean isInterface;

    private String templateEncoding = "UTF-8";
    private Template template;
    private String targetPath;

    /**
     * @param templatePath   the template
     * @param targetFileName the target file name
     * @param isInterface    true, if interface, false otherwise
     */
    public GenerationContext(String templatePath, String targetFileName, boolean isInterface) {
        this.templatePath = templatePath;
        this.targetFileName = targetFileName;
        this.targetPath = targetFileName + ".java";
        this.isInterface = isInterface;
    }

    /**
     * @return the targetFileName
     */
    public String getTargetFileName() {
        return this.targetFileName;
    }

    /**
     * @return the templatePath
     */
    public String getTemplatePath() {
        return this.templatePath;
    }

    /**
     * @return the targetPath
     */
    public String getTargetPath() {
        return this.targetPath;
    }

    /**
     * @return the isInterface
     */
    public boolean isInterface() {
        return this.isInterface;
    }

    /**
     * @return the encoding
     */
    public String getTemplateEncoding() {
        return this.templateEncoding;
    }

    /**
     * @param templateEncoding the encoding to set
     */
    public void setTemplateEncoding(String templateEncoding) {
        this.templateEncoding = templateEncoding;
    }

    /**
     * @param targetPath the targetPath to set
     */
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    /**
     * @return the template
     */
    public Template getTemplate() {
        return this.template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(Template template) {
        this.template = template;
    }

    /**
     * @param targetDir the target dir
     */
    public void setTargetDir(String targetDir) {
        File pckDir = new File(targetDir);
        if (!pckDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            pckDir.mkdirs();
        }
        this.targetPath = pckDir.getAbsolutePath() + File.separator + this.targetPath;
    }
}
