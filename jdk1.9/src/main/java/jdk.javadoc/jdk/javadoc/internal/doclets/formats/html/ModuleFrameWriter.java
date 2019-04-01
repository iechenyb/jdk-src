/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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

package jdk.javadoc.internal.doclets.formats.html;

import java.util.*;

import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;

import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlConstants;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTag;
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlTree;
import jdk.javadoc.internal.doclets.formats.html.markup.StringContent;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.DocFileIOException;
import jdk.javadoc.internal.doclets.toolkit.util.DocPaths;

/**
 * Class to generate file for each module contents in the left-hand bottom
 * frame. This will list all the Class Kinds in the module. A click on any
 * class-kind will update the right-hand frame with the clicked class-kind page.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 * @author Bhavesh Patel
 */
public class ModuleFrameWriter extends HtmlDocletWriter {

    /**
     * The module being documented.
     */
    private final ModuleElement mdle;

    /**
     * The classes to be documented.  Use this to filter out classes
     * that will not be documented.
     */
    private SortedSet<TypeElement> documentedClasses;

    /**
     * Constructor to construct ModuleFrameWriter object and to generate
     * "module_name-type-frame.html" file. For example for module "java.base" this will generate file
     * "java.base-type-frame.html" file.
     *
     * @param configuration the configuration of the doclet.
     * @param moduleElement moduleElement under consideration.
     */
    public ModuleFrameWriter(ConfigurationImpl configuration, ModuleElement moduleElement) {
        super(configuration, DocPaths.moduleTypeFrame(moduleElement));
        this.mdle = moduleElement;
        if (configuration.getSpecifiedPackageElements().isEmpty()) {
            documentedClasses = new TreeSet<>(utils.makeGeneralPurposeComparator());
            documentedClasses.addAll(configuration.getIncludedTypeElements());
        }
    }

    /**
     * Generate a module type summary page for the left-hand bottom frame.
     *
     * @param configuration the current configuration of the doclet.
     * @param moduleElement The package for which "module_name-type-frame.html" is to be generated.
     * @throws DocFileIOException if there is a problem generating the module summary file
     */
    public static void generate(ConfigurationImpl configuration, ModuleElement moduleElement)
            throws DocFileIOException {
        ModuleFrameWriter mdlgen = new ModuleFrameWriter(configuration, moduleElement);
        String mdlName = moduleElement.getQualifiedName().toString();
        Content mdlLabel = new StringContent(mdlName);
        HtmlTree body = mdlgen.getBody(false, mdlgen.getWindowTitle(mdlName));
        HtmlTree htmlTree = (configuration.allowTag(HtmlTag.MAIN))
                ? HtmlTree.MAIN()
                : body;
        Content heading = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING, HtmlStyle.bar,
                mdlgen.getHyperLink(DocPaths.moduleSummary(moduleElement), mdlLabel, "", "classFrame"));
        htmlTree.addContent(heading);
        HtmlTree div = new HtmlTree(HtmlTag.DIV);
        div.addStyle(HtmlStyle.indexContainer);
        mdlgen.addClassListing(div);
        htmlTree.addContent(div);
        if (configuration.allowTag(HtmlTag.MAIN)) {
            body.addContent(htmlTree);
        }
        mdlgen.printHtmlDocument(
                configuration.metakeywords.getMetaKeywordsForModule(moduleElement), false, body);
    }

    /**
     * Add class listing for all the classes in this module. Divide class
     * listing as per the class kind and generate separate listing for
     * Classes, Interfaces, Exceptions and Errors.
     *
     * @param contentTree the content tree to which the listing will be added
     */
    protected void addClassListing(HtmlTree contentTree) {
        List<PackageElement> packagesIn = ElementFilter.packagesIn(mdle.getEnclosedElements());
        SortedSet<TypeElement> interfaces = new TreeSet<>(utils.makeGeneralPurposeComparator());
        SortedSet<TypeElement> classes = new TreeSet<>(utils.makeGeneralPurposeComparator());
        SortedSet<TypeElement> enums = new TreeSet<>(utils.makeGeneralPurposeComparator());
        SortedSet<TypeElement> exceptions = new TreeSet<>(utils.makeGeneralPurposeComparator());
        SortedSet<TypeElement> errors = new TreeSet<>(utils.makeGeneralPurposeComparator());
        SortedSet<TypeElement> annotationTypes = new TreeSet<>(utils.makeGeneralPurposeComparator());
        for (PackageElement pkg : packagesIn) {
            if (utils.isIncluded(pkg)) {
                interfaces.addAll(utils.getInterfaces(pkg));
                classes.addAll(utils.getOrdinaryClasses(pkg));
                enums.addAll(utils.getEnums(pkg));
                exceptions.addAll(utils.getExceptions(pkg));
                errors.addAll(utils.getErrors(pkg));
                annotationTypes.addAll(utils.getAnnotationTypes(pkg));
            }
        }
        addClassKindListing(interfaces, contents.interfaces, contentTree);
        addClassKindListing(classes, contents.classes, contentTree);
        addClassKindListing(enums, contents.enums, contentTree);
        addClassKindListing(exceptions, contents.exceptions, contentTree);
        addClassKindListing(errors, contents.errors, contentTree);
        addClassKindListing(annotationTypes, contents.annotationTypes, contentTree);
    }

    /**
     * Add specific class kind listing. Also add label to the listing.
     *
     * @param list Iterable list of TypeElements
     * @param labelContent content tree of the label to be added
     * @param contentTree the content tree to which the class kind listing will be added
     */
    protected void addClassKindListing(Iterable<TypeElement> list, Content labelContent,
            HtmlTree contentTree) {
        SortedSet<TypeElement> tset = utils.filterOutPrivateClasses(list, configuration.javafx);
        if (!tset.isEmpty()) {
            boolean printedHeader = false;
            HtmlTree htmlTree = (configuration.allowTag(HtmlTag.SECTION))
                    ? HtmlTree.SECTION()
                    : contentTree;
            HtmlTree ul = new HtmlTree(HtmlTag.UL);
            ul.setTitle(labelContent);
            for (TypeElement typeElement : tset) {
                if (documentedClasses != null && !documentedClasses.contains(typeElement)) {
                    continue;
                }
                if (!utils.isCoreClass(typeElement) || !configuration.isGeneratedDoc(typeElement)) {
                    continue;
                }
                if (!printedHeader) {
                    Content heading = HtmlTree.HEADING(HtmlConstants.CONTENT_HEADING,
                            true, labelContent);
                    htmlTree.addContent(heading);
                    printedHeader = true;
                }
                Content arr_i_name = new StringContent(utils.getSimpleName(typeElement));
                if (utils.isInterface(typeElement)) {
                    arr_i_name = HtmlTree.SPAN(HtmlStyle.interfaceName, arr_i_name);
                }
                Content link = getLink(new LinkInfoImpl(configuration,
                        LinkInfoImpl.Kind.ALL_CLASSES_FRAME, typeElement).label(arr_i_name).target("classFrame"));
                Content li = HtmlTree.LI(link);
                ul.addContent(li);
            }
            htmlTree.addContent(ul);
            if (configuration.allowTag(HtmlTag.SECTION)) {
                contentTree.addContent(htmlTree);
            }
        }
    }
}
