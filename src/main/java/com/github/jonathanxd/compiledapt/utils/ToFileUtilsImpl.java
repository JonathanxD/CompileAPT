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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by jonathan on 24/04/16.
 */
public class ToFileUtilsImpl implements ToFileUtils {


    @Override
    public File[] toFile(URL[] uris) {

        return Arrays.stream(uris)
                .filter(url -> {
                    try {
                        url.toURI();
                        return true;
                    } catch (Throwable t) {
                        return false;
                    }
                })
                .map(url -> {
                    try {
                        return url.toURI();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }).filter(url -> {
                    try {
                        File file = new File(url);
                        if (file.exists()) {
                            return true;
                        }
                    } catch (Throwable ignored) {
                    }

                    return false;
                }).map(File::new).toArray(File[]::new);

    }

    @Override
    public File getRootFromPackage(String thePackage, File current) {
        String path = current.getAbsolutePath();
        thePackage = thePackage.replace('.', File.separatorChar);

        if(path.endsWith(thePackage)) {
            int i = path.lastIndexOf(thePackage);

            return new File(path.substring(0, i));
        }

        return null;
    }

}
