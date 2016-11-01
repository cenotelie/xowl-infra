/*******************************************************************************
 * Copyright (c) 2016 Association Cénotélie (cenotelie.fr)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.xowl.infra.generator.builder;

import org.xowl.infra.utils.Files;
import org.xowl.infra.utils.logging.Logger;

import javax.tools.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Build tool for Java artifacts
 *
 * @author Laurent Wouters
 */
public class Builder {
    /**
     * The file extension of Java source files
     */
    private static final String JAVA_FILE_EXTENSION = ".java";

    /**
     * The source folder
     */
    private final File folderSrc;
    /**
     * The build folder
     */
    private final File folderBuild;
    /**
     * The binary results folder
     */
    private final File folderBin;
    /**
     * Whether to emit debuggable bytecode
     */
    private final boolean debug;
    /**
     * Java file manager
     */
    private final StandardJavaFileManager fileManager;
    /**
     * The libraries to link against
     */
    private final List<String> libraries;

    /**
     * Adds a library to link against
     *
     * @param file The jar file of the library
     */
    public void addLibrary(String file) {
        libraries.add(file);
    }

    /**
     * Initializes this builder
     *
     * @param projectFolder The folder containing the project to build
     * @param debug         Whether to emit debuggable bytecode
     */
    public Builder(File projectFolder, boolean debug) {
        this.libraries = new ArrayList<>();
        this.folderSrc = new File(projectFolder, "src");
        this.folderBuild = new File(projectFolder, "build");
        this.folderBin = new File(projectFolder, "dist");
        this.debug = debug;
        this.fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(new DiagnosticCollector<JavaFileObject>(), null, null);
    }

    /**
     * Builds the project to the specified jar name
     *
     * @param logger  The logger to use
     * @param jarName The name of the jar to generate
     * @throws IOException          When an IO error occurs
     * @throws InterruptedException When the process is interrupted
     */
    public void build(Logger logger, String jarName) throws IOException, InterruptedException {
        // compiling
        if (folderBuild.exists())
            deleteDirectory(folderBuild);
        if (!folderBuild.mkdirs())
            throw new IOException("Failed to create directory " + folderBuild.getAbsolutePath());
        OutputStreamWriter writer = new OutputStreamWriter(System.out, Files.CHARSET);
        JavaCompiler.CompilationTask task = ToolProvider.getSystemJavaCompiler().getTask(
                writer,
                fileManager,
                null,
                buildJavacParameters(),
                null,
                fileManager.getJavaFileObjectsFromFiles(buildSourcesList()));
        boolean success = task.call();
        if (!success) {
            logger.error("Failed to build");
            return;
        }

        // creating the jar
        if (folderBin.exists())
            deleteDirectory(folderBin);
        if (!folderBin.mkdirs())
            throw new IOException("Failed to create directory " + folderBin.getAbsolutePath());
        JarNodeDirectory root = new JarNodeDirectory("");
        buildJarTree(root, folderBuild, "", false);
        try (FileOutputStream output = new FileOutputStream(new File(folderBin, jarName + ".jar"))) {
            Manifest manifest = new Manifest();
            Attributes manifestAttr = manifest.getMainAttributes();
            manifestAttr.putValue("Manifest-Version", "1.0");
            manifestAttr.putValue("Class-Path", buildClasspath());
            JarOutputStream jarStream = new JarOutputStream(output, manifest);
            root.createEntry(jarStream);
            jarStream.flush();
            jarStream.close();
        }
    }

    /**
     * Gets the list of Java files to compile
     *
     * @return The Java files to compile
     */
    private List<File> buildSourcesList() {
        List<File> sources = new ArrayList<>();
        buildSourcesListFromDirectory(sources, folderSrc);
        return sources;
    }

    /**
     * Builds the list of sources in the specified directory
     *
     * @param sources   The list of sources to build
     * @param directory The directory to inspect
     */
    private void buildSourcesListFromDirectory(List<File> sources, File directory) {
        File[] files = directory.listFiles();
        if (files == null)
            return;
        for (File element : files) {
            if (element.isDirectory())
                buildSourcesListFromDirectory(sources, element);
            else if (element.getName().endsWith(JAVA_FILE_EXTENSION))
                sources.add(element);
        }
    }

    /**
     * Builds the list of parameters for javac
     *
     * @return The parameters for javac
     */
    private List<String> buildJavacParameters() {
        List<String> options = new ArrayList<>();
        if (debug)
            options.add("-g");
        else
            options.add("-g:none");
        options.add("-d");
        options.add(folderBuild.getPath());
        options.add("-encoding");
        options.add("UTF-8");
        if (libraries.size() != 0) {
            options.add("-classpath");
            String classpath = "";
            for (int i = 0; i != libraries.size(); i++) {
                if (i != 0) classpath += ";";
                classpath += libraries.get(i);
            }
            options.add(classpath);
        }
        return options;
    }

    /**
     * Builds the classpath for this project
     *
     * @return The class path for this project
     */
    private String buildClasspath() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i != libraries.size(); i++) {
            File file = new File(libraries.get(i));
            if (i != 0)
                buffer.append(";");
            buffer.append("lib/");
            buffer.append(file.getName());
        }
        return buffer.toString();
    }

    /**
     * Deletes the specified directory and its content, if any
     *
     * @param directory The directory to delete
     */
    private void deleteDirectory(File directory) throws IOException {
        if (!directory.exists())
            return;
        File[] children = directory.listFiles();
        if (children != null) {
            for (int i = 0; i != children.length; i++) {
                if (children[i].isDirectory())
                    deleteDirectory(children[i]);
                else if (!children[i].delete()) {
                    throw new IOException("Failed to delete " + children[i].getPath());
                }
            }
        }
        if (!directory.delete())
            throw new IOException("Failed to delete " + directory.getPath());
    }

    /**
     * Builds the jar tree
     *
     * @param root        The root jar node
     * @param directory   The current directory to inspect
     * @param localPath   The local path so far
     * @param createEntry Whether to create an entry for this directory
     */
    private void buildJarTree(JarNodeDirectory root, File directory, String localPath, boolean createEntry) {
        if (createEntry)
            root.add(localPath);
        File[] children = directory.listFiles();
        if (children != null) {
            for (File element : children) {
                if (element.isDirectory())
                    buildJarTree(
                            root,
                            element,
                            localPath + element.getName() + JarNodeDirectory.SEPARATOR,
                            true);
                else
                    root.add(localPath + element.getName(), element);
            }
        }
    }
}
