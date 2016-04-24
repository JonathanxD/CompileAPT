/*
 *      CompileAPT - Hackly pre-compile source codes during Annotation Processing for an Abstract Manipulation <https://github.com/JonathanxD/CompileAPT>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2016 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.compiledapt.internals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * Created by jonathan on 22/04/16.
 */
public class MemoryCompiler {
    private javax.tools.JavaCompiler tool;
    private StandardJavaFileManager stdManager;

    public MemoryCompiler() {
        tool = ToolProvider.getSystemJavaCompiler();
        if (tool == null) {
            throw new RuntimeException("Could not get Java compiler. Please, ensure that JDK is used instead of JRE.");
        }
        stdManager = tool.getStandardFileManager(null, null, null);
    }

    /**
     * Compile a single static method.
     */
    public Method compileStaticMethod(final String methodName, final String className,
                                      final String source)
            throws ClassNotFoundException {
        final Map<String, byte[]> classBytes = compile(className + ".java", source);
        final MemoryClassLoader classLoader = new MemoryClassLoader(classBytes);
        final Class clazz = classLoader.loadClass(className);
        final Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (!method.isAccessible()) method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodError(methodName);
    }


    public Map<String, byte[]> compile(String fileName, String source, String sourcePath, String classPath) {
        return compile(fileName, source, new PrintWriter(System.err), sourcePath, classPath);
    }

    public Map<String, byte[]> compileWithClasspath(String fileName, String source, String classPath) {
        return compile(fileName, source, new PrintWriter(System.err), null, classPath);
    }

    public Map<String, byte[]> compileWithClasspath(String fileName, String source, String classPath, PrintWriter err) {
        return compile(fileName, source, err, null, classPath);
    }

    public Map<String, byte[]> compileWithSourcepath(String fileName, String source, String sourcePath) {
        return compile(fileName, source, new PrintWriter(System.err), sourcePath, null);
    }

    public Map<String, byte[]> compile(String fileName, String source) {
        return compile(fileName, source, new PrintWriter(System.err), null, null);
    }


    private Map<String, byte[]> compile(String fileName, String source,
                                        Writer err, String sourcePath, String classPath) {
        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<JavaFileObject>();

        MemoryFileMan fileManager = new MemoryFileMan(stdManager);

        List<JavaFileObject> compUnits = new ArrayList<JavaFileObject>(1);
        compUnits.add(fileManager.makeStringSource(fileName, source));

        return compile(compUnits, fileManager, err, sourcePath, classPath);
    }

    private Map<String, byte[]> compile(final List<JavaFileObject> compUnits,
                                        final MemoryFileMan fileManager,
                                        Writer err, String sourcePath, String classPath) {

        DiagnosticCollector<JavaFileObject> diagnostics =
                new DiagnosticCollector<JavaFileObject>();

        List<String> options = new ArrayList<String>();
        options.add("-Xlint:all");

        options.add("-deprecation");
        if (sourcePath != null) {
            options.add("-sourcepath");
            options.add(sourcePath);
        }

        if (classPath != null) {
            options.add("-classpath");
            options.add(classPath);
        }

        // create a compilation task
        javax.tools.JavaCompiler.CompilationTask task =
                tool.getTask(err, fileManager, diagnostics,
                        options, null, compUnits);

        if (task.call() == false) {
            PrintWriter perr = new PrintWriter(err);
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                perr.println(diagnostic);
            }
            perr.flush();
            return null;
        }

        Map<String, byte[]> classBytes = fileManager.getClassBytes();
        try {
            fileManager.close();
        } catch (IOException exp) {
        }

        return classBytes;
    }
}