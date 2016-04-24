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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by jonathan on 22/04/16.
 */
public final class MemoryClassLoader extends URLClassLoader {
    private Map<String, byte[]> classBytes;

    public MemoryClassLoader(Map<String, byte[]> classBytes,
                             String classPath, ClassLoader parent) {
        super(toURLs(classPath), parent);
        this.classBytes = classBytes;
    }

    public MemoryClassLoader(Map<String, byte[]> classBytes, String classPath) {
        this(classBytes, classPath, ClassLoader.getSystemClassLoader());
    }

    public MemoryClassLoader(Map<String, byte[]> classBytes) {
        this(classBytes, null, ClassLoader.getSystemClassLoader());
    }

    private static URL[] toURLs(String classPath) {
        if (classPath == null) {
            return new URL[0];
        }

        List<URL> list = new ArrayList<URL>();
        StringTokenizer st = new StringTokenizer(classPath, File.pathSeparator);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            File file = new File(token);
            if (file.exists()) {
                try {
                    list.add(file.toURI().toURL());
                } catch (MalformedURLException mue) {
                }
            } else {
                try {
                    list.add(new URL(token));
                } catch (MalformedURLException mue) {
                }
            }
        }
        URL[] res = new URL[list.size()];
        list.toArray(res);
        return res;
    }

    public Class load(String className) throws ClassNotFoundException {
        return loadClass(className);
    }

    public Iterable<Class> loadAll() throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>(classBytes.size());
        for (String name : classBytes.keySet()) {
            classes.add(loadClass(name));
        }
        return classes;
    }

    protected Class findClass(String className) throws ClassNotFoundException {
        byte[] buf = classBytes.get(className);
        if (buf != null) {
            classBytes.put(className, null);
            return defineClass(className, buf, 0, buf.length);
        } else {
            return super.findClass(className);
        }
    }
}