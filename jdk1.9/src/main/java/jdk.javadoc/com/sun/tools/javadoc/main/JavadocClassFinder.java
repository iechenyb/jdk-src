/*
 * Copyright (c) 2001, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.tools.javadoc.main;

import java.util.EnumSet;
import javax.tools.JavaFileObject;

import com.sun.tools.javac.code.Symbol.PackageSymbol;
import com.sun.tools.javac.code.ClassFinder;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Context.Factory;

/** Javadoc uses an extended class finder that records package.html entries
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 *  @author Neal Gafter
 */
@Deprecated
public class JavadocClassFinder extends ClassFinder {

    public static JavadocClassFinder instance(Context context) {
        ClassFinder instance = context.get(classFinderKey);
        if (instance == null)
            instance = new JavadocClassFinder(context);
        return (JavadocClassFinder)instance;
    }

    public static void preRegister(Context context) {
        context.put(classFinderKey, (Factory<ClassFinder>)JavadocClassFinder::new);
    }

    private DocEnv docenv;
    private EnumSet<JavaFileObject.Kind> all = EnumSet.of(JavaFileObject.Kind.CLASS,
                                                          JavaFileObject.Kind.SOURCE,
                                                          JavaFileObject.Kind.HTML);
    private EnumSet<JavaFileObject.Kind> noSource = EnumSet.of(JavaFileObject.Kind.CLASS,
                                                               JavaFileObject.Kind.HTML);

    public JavadocClassFinder(Context context) {
        super(context);
        docenv = DocEnv.instance(context);
        preferSource = true;
    }

    /**
     * Override getPackageFileKinds to include search for package.html
     */
    @Override
    protected EnumSet<JavaFileObject.Kind> getPackageFileKinds() {
        return docenv.docClasses ? noSource : all;
    }

    /**
     * Override extraFileActions to check for package documentation
     */
    @Override
    protected void extraFileActions(PackageSymbol pack, JavaFileObject fo) {
        if (fo.isNameCompatible("package", JavaFileObject.Kind.HTML))
            docenv.getPackageDoc(pack).setDocPath(fo);
    }
}
