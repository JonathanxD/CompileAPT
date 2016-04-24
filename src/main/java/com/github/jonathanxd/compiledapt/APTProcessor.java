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
package com.github.jonathanxd.compiledapt;

import com.github.jonathanxd.compiledapt.env.CompiledProcessingEnv;
import com.github.jonathanxd.compiledapt.exceptions.CannotCompileException;
import com.github.jonathanxd.compiledapt.internals.MemoryClassLoader;
import com.github.jonathanxd.compiledapt.internals.MemoryCompiler;
import com.github.jonathanxd.compiledapt.service.CompiledProcessor;
import com.github.jonathanxd.compiledapt.unknown.UnknownElementState;
import com.github.jonathanxd.compiledapt.utils.ClassPathUtils;
import com.github.jonathanxd.compiledapt.utils.ClassPathUtilsImpl;
import com.github.jonathanxd.compiledapt.utils.ElementUtils;
import com.github.jonathanxd.compiledapt.utils.ElementUtilsImpl;
import com.github.jonathanxd.compiledapt.utils.JunStringUtil;
import com.github.jonathanxd.compiledapt.utils.SourceUtils;
import com.github.jonathanxd.compiledapt.utils.SourceUtilsImpl;
import com.github.jonathanxd.compiledapt.utils.ToFileUtils;
import com.github.jonathanxd.compiledapt.utils.ToFileUtilsImpl;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Created by jonathan on 24/04/16.
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class APTProcessor extends AbstractProcessor {

    private final ElementUtils elementUtils = new ElementUtilsImpl();
    private final ClassPathUtils classPathUtils = new ClassPathUtilsImpl();
    private final SourceUtils sourceUtils = new SourceUtilsImpl();
    private final ToFileUtils toFileUtils = new ToFileUtilsImpl();

    private Messager messager = null;

    private Trees trees;
    private Set<CompiledProcessor> loaded = new HashSet<>();

    private CompiledProcessingEnv compiledProcessingEnv;

    public APTProcessor() {

    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        messager = processingEnv.getMessager();

        compiledProcessingEnv = new CompiledProcessingEnv(classPathUtils, sourceUtils, elementUtils, toFileUtils, processingEnv);

        updatedServices();

    }

    public void updatedServices() {
        ServiceLoader<CompiledProcessor> localLoad = ServiceLoader.load(CompiledProcessor.class, APTProcessor.class.getClassLoader());

        for (CompiledProcessor compiledProcessor : localLoad) {
            if(!loaded.stream().map(Object::toString).collect(Collectors.toList()).contains(compiledProcessor.toString())) {

                messager.printMessage(Diagnostic.Kind.OTHER, "Loading processor '"+compiledProcessor.getClass().getCanonicalName()+"'...");

                compiledProcessor.init(compiledProcessingEnv);

                messager.printMessage(Diagnostic.Kind.OTHER, "Loaded!");

                loaded.add(compiledProcessor);

            }
        }

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        updatedServices();

        messager.printMessage(Diagnostic.Kind.NOTE, "Processing annotations '"+annotations+"'...");

        for (CompiledProcessor compiledProcessor : loaded) {

            Map<TypeElement, File> elements = findElements(annotations, compiledProcessor, roundEnv);

            elements.forEach((typeElement, file) -> {

                Class<? extends Annotation>[] supportedAnnotations = compiledProcessor.getSupportedAnnotations();

                ClassLoader cl;

                if(supportedAnnotations.length == 0) {
                    cl = ClassLoader.getSystemClassLoader();
                } else {
                    cl = supportedAnnotations[0].getClassLoader();
                }

                try {
                    Set<Class<?>> compile = compile(file, typeElement, cl);

                    compiledProcessor.process(new UnknownElementState<>(compile, file), typeElement, roundEnv);

                } catch (IOException e) {
                    compiledProcessor.process(new UnknownElementState<>(file), typeElement, roundEnv);

                    messager.printMessage(Diagnostic.Kind.ERROR, e.getLocalizedMessage());
                }

            });
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "Process end!");

        return false;
    }

    private Set<Class<?>> compile(File file, Element element, ClassLoader selectedCl) throws IOException {
        String fileName = file.getName().substring(file.getName().lastIndexOf('/') + 1, file.getName().length());
        String[] source = sourceUtils.getSource(file);
        String aPackage = elementUtils.getPackage(element);
        String sourcej = JunStringUtil.appendAll(source, "\n");

        String fullName = aPackage + "." + fileName;

        URL[] urls = classPathUtils.getUrls(selectedCl);

        String classPathString = JunStringUtil.toString(toFileUtils.toFile(urls), file1 -> file1.toURI().toString(), ":");

        MemoryCompiler memoryCompiler = new MemoryCompiler();

        File sources;

        if(file.getAbsoluteFile().getParentFile() == null) {
            sources = new File(".");
        }else {
            sources = toFileUtils.getRootFromPackage(aPackage, file.getAbsoluteFile().getParentFile());
        }

        Map<String, byte[]> compile = Objects.requireNonNull(memoryCompiler.compile(fullName, sourcej, sources.getPath(), classPathString), "Cannot compile!");

        MemoryClassLoader memoryClassLoader = new MemoryClassLoader(compile, classPathString, selectedCl);

        try {
            Iterable<Class> classes = memoryClassLoader.loadAll();

            Set<Class<?>> classSet = new HashSet<>();

            classes.forEach(classSet::add);

            return classSet;

        } catch (ClassNotFoundException e) {
            throw new CannotCompileException("Cannot compile!", e);
        }


    }

    @SuppressWarnings("unchecked")
    private Map<TypeElement, File> findElements(Set<? extends TypeElement> annotations, CompiledProcessor compiledProcessor, RoundEnvironment roundEnv) {

        Map<TypeElement, File> elementFileMap = new HashMap<>();

        Class<? extends Annotation>[] supportedAnnotations = compiledProcessor.getSupportedAnnotations();

        TypeElement[] annotationsProcess = Arrays.stream(supportedAnnotations).filter(aClass -> {
            for (TypeElement annotation : annotations) {
                if(aClass.getCanonicalName().equals(annotation.getQualifiedName().toString())) {
                    return true;
                }
            }

            return false;
        }).map(aClass -> {
            for (TypeElement annotation : annotations) {
                if(aClass.getCanonicalName().equals(annotation.getQualifiedName().toString())) {
                    return annotation;
                }
            }
            return (TypeElement) null;
        }).toArray(TypeElement[]::new);

        for (TypeElement supportedAnnotation : annotationsProcess) {

            Set<? extends Element> bindAnnotated = roundEnv.getElementsAnnotatedWith(supportedAnnotation);

            if(bindAnnotated.size() == 0) {
                messager.printMessage(Diagnostic.Kind.OTHER, "Cannot find elements annotated with '"+supportedAnnotation+"'");
            }

            bindAnnotated.forEach(o -> {

                TreePath path = trees.getPath(o);

                JavaFileObject sourceFile = path.getCompilationUnit().getSourceFile();

                URI uri = sourceFile.toUri();
                try {

                    TypeElement typeElement = elementUtils.getTypeElement(o);

                    if (!elementFileMap.containsKey(typeElement)) {
                        File file = new File(uri);
                        elementFileMap.put(typeElement, file);
                    }

                } catch (Throwable t) {
                    messager.printMessage(Diagnostic.Kind.NOTE, "Cannot get file from uri '" + uri + "'.");
                }

            });
        }

        return elementFileMap;
    }

}
