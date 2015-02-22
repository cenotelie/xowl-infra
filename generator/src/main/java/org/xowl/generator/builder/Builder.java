/**********************************************************************
 * Copyright (c) 2015 Laurent Wouters
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
 *
 * Contributors:
 *     Laurent Wouters - lwouters@xowl.org
 **********************************************************************/

package org.xowl.generator.builder;

import javax.tools.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
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
    private String folderSrc;
    /**
     * The build folder
     */
    private String folderBuild;
    /**
     * The binary results folder
     */
    private String folderBin;
    /**
     * Whether to emit debuggable bytecode
     */
    private boolean debug;
    /**
     * Java file manager
     */
    private StandardJavaFileManager fileManager;
    /**
     * The libraries to link against
     */
    private List<String> libraries;
    /**
     * The root output node
     */
    private JarNodeDirectory root;

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
    public Builder(String projectFolder, boolean debug) {
        this.libraries = new ArrayList<>();
        this.folderSrc = projectFolder + "src/";
        this.folderBuild = projectFolder + "build/";
        this.folderBin = projectFolder + "dist/";
        this.debug = debug;
        this.fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(new DiagnosticCollector<JavaFileObject>(), null, null);
    }

    /**
     * Builds the project to the specified jar name
     *
     * @param jarName The name of the jar to generate
     * @throws java.io.IOException            When an IO error occurs
     * @throws java.lang.InterruptedException When the process is interrupted
     */
    public void build(String jarName) throws java.io.IOException, java.lang.InterruptedException {
        // compiling
        File directory = new File(folderBuild);
        if (directory.exists())
            deleteDirectory(directory);
        directory.mkdir();
        OutputStreamWriter writer = new OutputStreamWriter(System.out, Charset.forName("UTF-8"));
        List<String> options = buildJavacParameters();
        JavaCompiler.CompilationTask task = ToolProvider.getSystemJavaCompiler().getTask(writer, fileManager, null, options, null, fileManager.getJavaFileObjectsFromFiles(buildSourcesList()));
        task.call();

        // creating the jar
        directory = new File(folderBin);
        if (directory.exists())
            deleteDirectory(directory);
        directory.mkdir();
        root = new JarNodeDirectory("");
        buildJarTree(new File(folderBuild), "", false);
        try {
            FileOutputStream output = new FileOutputStream(folderBin + jarName + ".jar");
            Manifest manifest = new Manifest();
            Attributes manifestAttr = manifest.getMainAttributes();
            manifestAttr.putValue("Manifest-Version", "1.0");
            manifestAttr.putValue("Class-Path", buildClasspath());
            JarOutputStream jarStream = new JarOutputStream(output, manifest);
            root.createEntry(jarStream);
            jarStream.flush();
            jarStream.close();
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the list of Java files to compile
     *
     * @return The Java files to compile
     */
    private List<File> buildSourcesList() {
        List<File> sources = new ArrayList<>();
        buildSourcesListFromDirectory(sources, new File(folderSrc));
        return sources;
    }

    /**
     * Builds the list of sources in the specified directory
     *
     * @param sources   The list of sources to build
     * @param directory The directory to inspect
     */
    private void buildSourcesListFromDirectory(List<File> sources, File directory) {
        for (File element : directory.listFiles()) {
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
        options.add(folderBuild);
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
    private void deleteDirectory(File directory) {
        File[] children = directory.listFiles();
        if (children == null)
            return;
        for (int i = 0; i != children.length; i++) {
            if (children[i].isDirectory())
                deleteDirectory(children[i]);
            else if (!children[i].delete()) {
                System.err.println("failed to delete: " + children[i].toString());
            }
        }
        directory.delete();
    }

    /**
     * Builds the jar tree
     *
     * @param directory   The current directory to inspect
     * @param localPath   The local path so far
     * @param createEntry Whether to create an entry for this directory
     */
    private void buildJarTree(File directory, String localPath, boolean createEntry) {
        if (createEntry)
            root.add(localPath);
        for (java.io.File element : directory.listFiles()) {
            if (element.isDirectory())
                buildJarTree(element, localPath + element.getName() + JarNodeDirectory.SEPARATOR, true);
            else
                root.add(localPath + element.getName(), element);
        }
    }
}
