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
package com.github.jonathanxd.compiledapt.env;

import com.github.jonathanxd.compiledapt.utils.ClassPathUtils;
import com.github.jonathanxd.compiledapt.utils.ElementUtils;
import com.github.jonathanxd.compiledapt.utils.SourceUtils;
import com.github.jonathanxd.compiledapt.utils.ToFileUtils;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * Created by jonathan on 24/04/16.
 */
public class CompiledProcessingEnv {

    private final ClassPathUtils classPathUtils;
    private final SourceUtils sourceUtils;
    private final ElementUtils elementUtils;
    private final ToFileUtils toFileUtils;
    private final ProcessingEnvironment processingEnvironment;

    public CompiledProcessingEnv(ClassPathUtils classPathUtils, SourceUtils sourceUtils, ElementUtils elementUtils, ToFileUtils toFileUtils, ProcessingEnvironment processingEnvironment) {
        this.classPathUtils = classPathUtils;
        this.sourceUtils = sourceUtils;
        this.elementUtils = elementUtils;
        this.toFileUtils = toFileUtils;
        this.processingEnvironment = processingEnvironment;
    }

    public ClassPathUtils getClassPathUtils() {
        return classPathUtils;
    }

    public ElementUtils getElementUtils() {
        return elementUtils;
    }

    public SourceUtils getSourceUtils() {
        return sourceUtils;
    }

    public ToFileUtils getToFileUtils() {
        return toFileUtils;
    }

    public ProcessingEnvironment getProcessingEnvironment() {
        return processingEnvironment;
    }
}
