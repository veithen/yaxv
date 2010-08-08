/*
 * Copyright 2006-2010 Andreas Veithen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.yaxv.mkresources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;

/**
 * @goal generate
 * @phase generate-sources
 */
public class GenerateMojo extends AbstractMojo {
    /**
     * @parameter expression="${project.resources}"
     * @required
     * @readonly
     */
    private List<Resource> resources;
    
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * @parameter
     */
    private String[] includes;
    
    /**
     * @parameter
     */
    private String[] excludes;
    
    /**
     * @parameter expression="${project.build.directory}/generated-sources/mkresources"
     * @required
     */
    private File outputDirectory;
    
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log log = getLog();
        boolean doAddSources = false;
        for (Resource resource : resources) {
            File dir = new File(resource.getDirectory());
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(resource.getDirectory());
            scanner.setIncludes(includes != null ? includes : new String[] { "**/*.properties" } );
            scanner.setExcludes(excludes);
            scanner.scan();
            String[] includedFiles = scanner.getIncludedFiles();
            if (includedFiles.length > 0) {
                URL url;
                try {
                    url = dir.toURI().toURL();
                } catch (MalformedURLException ex) {
                    throw new MojoExecutionException("Unexpected exception", ex);
                }
                ClassLoader loader = new URLClassLoader(new URL[] { url });
                for (String file : includedFiles) {
                    if (file.endsWith(".properties")) {
                        String[] baseNameComponents = file.substring(0, file.length()-11).split("[/\\\\]");
                        String baseName = StringUtils.join(baseNameComponents, ".");
                        log.info("Generating constants for resource bundle " + baseName);
                        ResourceBundle messages = ResourceBundle.getBundle(baseName, Locale.ENGLISH, loader);
                        String className = baseNameComponents[baseNameComponents.length-1];
                        className = className.substring(0, 1).toUpperCase() + className.substring(1);
                        File outputFile = outputDirectory;
                        StringBuilder packageName = new StringBuilder();
                        for (int i=0; i<baseNameComponents.length-1; i++) {
                            if (i > 0) {
                                packageName.append('.');
                            }
                            String component = baseNameComponents[i];
                            packageName.append(component);
                            outputFile = new File(outputFile, component);
                        }
                        outputFile.mkdirs();
                        outputFile = new File(outputFile, className + ".java");
                        try {
                            generate(baseName, messages, outputFile, packageName.toString(), className);
                        } catch (IOException ex) {
                            throw new MojoExecutionException("Failed to generate " + outputFile + ": " + ex.getMessage(), ex);
                        }
                        doAddSources = true;
                    } else {
                        log.warn("Skipping file " + file + ": not a resource bundle");
                    }
                }
            }
        }
        if (doAddSources) {
            project.addCompileSourceRoot(outputDirectory.getPath());
        }
    }
    
    private void generate(String baseName, ResourceBundle messages, File outputFile, String packageName, String className) throws IOException {
        Set<String> sortedKeys = new TreeSet<String>();
        for (Enumeration<String> keys = messages.getKeys(); keys.hasMoreElements(); ) {
            sortedKeys.add(keys.nextElement());
        }
        OutputStream out = new FileOutputStream(outputFile);
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), false);
            writer.println("package " + packageName + ";");
            writer.println();
            writer.println("import java.util.ResourceBundle;");
            writer.println();
            writer.println("public final class " + className + "{");
            writer.println("    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(\"" + baseName + "\");");
            writer.println();
            StringBuilder buff = new StringBuilder();
            for (String key : sortedKeys) {
                buff.setLength(0);
                for (int i=0; i<key.length(); i++) {
                    char c = key.charAt(i);
                    if (c == '.') {
                        buff.append('_');
                    } else if ('A' <= c && c <= 'Z') {
                        buff.append(c);
                    } else if ('a' <= c && c <= 'z') {
                        buff.append((char)(c-'a'+'A'));
                    }
                }
                writer.println("    public static final String " + buff + " = \"" + key + "\";");
            }
            writer.println("}");
            writer.flush();
        } finally {
            out.close();
        }
    }
}
