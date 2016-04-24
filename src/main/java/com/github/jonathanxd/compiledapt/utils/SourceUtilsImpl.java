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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Created by jonathan on 24/04/16.
 */
public class SourceUtilsImpl implements SourceUtils {

    private static final String CLASS_DECL = "public class ";

    @Override
    public String[] getSource(File file) throws IOException {
        return Files.readAllLines(file.toPath()).stream().toArray(String[]::new);
    }

    public String getClassName(String[] source) {
        for (String sourcePart : source) {
            if(sourcePart.startsWith(CLASS_DECL)) {
                return parseClassName(sourcePart);
            }
        }

        throw new IllegalSourceCodeException(Arrays.toString(source));
    }

    public String parseClassName(String s) {

        if(s.startsWith(CLASS_DECL)) {
            String substring = s.substring(CLASS_DECL.length());

            StringBuilder nameBuilder = new StringBuilder();

            char[] chars = substring.toCharArray();

            for (char aChar : chars) {
                if(Character.isLetterOrDigit(aChar) || aChar == '$') {
                    nameBuilder.append(aChar);
                } else {
                    return nameBuilder.toString();
                }
            }

        }

        return null;
    }

    public String getPackage(String[] source) {
        for (String sourcePart : source) {
            if(sourcePart.startsWith("package")) {
                return sourcePart.substring("package".length()+1, sourcePart.indexOf(';'));
            }
        }

        throw new IllegalSourceCodeException(Arrays.toString(source));
    }
}
