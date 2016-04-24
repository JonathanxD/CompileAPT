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
package com.github.jonathanxd.compiledapt.utils;

import com.github.jonathanxd.compiledapt.utils.exceptions.IllegalSourceCodeException;

import java.util.Objects;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * Created by jonathan on 24/04/16.
 */
public class ElementUtilsImpl implements ElementUtils {

    @Override
    public TypeElement getTypeElement(Element e) {

        Element enclosing = e.getEnclosingElement();

        Objects.requireNonNull(enclosing, "Package element reached!");


        if(e instanceof TypeElement && enclosing instanceof PackageElement)
            return (TypeElement) e;

        Element enclosingElement = e.getEnclosingElement();

        do {

            enclosing = e.getEnclosingElement();

            Objects.requireNonNull(enclosing, "Package element reached!");

            if(enclosingElement instanceof TypeElement && enclosing instanceof PackageElement) {
                return (TypeElement) enclosingElement;
            }
        } while ((enclosingElement = e.getEnclosingElement()) != null);

        return (TypeElement) enclosingElement;
    }

    @Override
    public String getClassName(Element element) throws IllegalSourceCodeException {
        TypeElement typeElement = getTypeElement(element);
        return typeElement.getSimpleName().toString();
    }

    @Override
    public String getPackage(Element element) throws IllegalSourceCodeException {
        TypeElement typeElement = getTypeElement(element);
        return ((PackageElement)typeElement.getEnclosingElement()).getQualifiedName().toString();
    }
}
