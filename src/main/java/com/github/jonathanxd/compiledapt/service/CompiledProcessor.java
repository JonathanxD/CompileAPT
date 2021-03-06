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
package com.github.jonathanxd.compiledapt.service;

import com.github.jonathanxd.compiledapt.env.CompiledProcessingEnv;
import com.github.jonathanxd.compiledapt.unknown.UnknownElementState;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * Created by jonathan on 24/04/16.
 */
public interface CompiledProcessor {

    default void init(CompiledProcessingEnv compiledProcessingEnv) {

    }

    default void preProcess(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

    }

    default ClassLoader getClassLoader(File fileToLoad) {
        return this.getClass().getClassLoader();
    }

    void process(UnknownElementState<Set<Class<?>>, File> compiledUnresolved, TypeElement element, RoundEnvironment roundEnvironment);

    Class<? extends Annotation>[] getSupportedAnnotations();

    default String[] sourcesDir() {
        return new String[0];
    }

}
