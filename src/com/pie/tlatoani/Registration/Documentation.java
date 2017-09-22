package com.pie.tlatoani.Registration;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Created by Tlatoani on 9/9/17.
 */
public final class Documentation {
    public static final Comparator<DocumentationElement> DOCUMENTATION_ELEMENT_COMPARATOR = Comparator.comparing(docElem -> docElem.category);
    public static final int ELEMENTS_PER_PAGE = 8;

    private static List<DocumentationBuilder> builders = new ArrayList<>();
    private static boolean built = false;

    private static SortedSet<String> categories = null;
    private static ListMultimap<String, DocumentationElement> allElements = null;

    private static ListMultimap<String, DocumentationElement.Effect> effects = null;
    private static ListMultimap<String, DocumentationElement.Expression> expressions = null;
    private static ListMultimap<String, DocumentationElement.Event> events = null;
    private static ListMultimap<String, DocumentationElement.Type> types = null;

    static void addBuilder(DocumentationBuilder builder) {
        builders.add(builder);
    }

    public static void buildDocumentation() {
        if (built) {
            throw new IllegalStateException("The documentation has already been built");
        }
        categories = new TreeSet<>();
        allElements = ArrayListMultimap.create();
        effects = ArrayListMultimap.create();
        expressions = ArrayListMultimap.create();
        events = ArrayListMultimap.create();
        types = ArrayListMultimap.create();
        for (DocumentationBuilder builder : builders) {
            DocumentationElement docElem = builder.build();
            categories.add(docElem.category);
            allElements.put(docElem.category, docElem);
            if (docElem instanceof DocumentationElement.Effect) {
                effects.put(docElem.category, (DocumentationElement.Effect) docElem);
            } else if (docElem instanceof DocumentationElement.Expression) {
                expressions.put(docElem.category, (DocumentationElement.Expression) docElem);
            } else if (docElem instanceof DocumentationElement.Event) {
                events.put(docElem.category, (DocumentationElement.Event) docElem);
            } else if (docElem instanceof DocumentationElement.Type) {
                types.put(docElem.category, (DocumentationElement.Type) docElem);
            }
        }
        MundoUtil.sortListMultimap(allElements, DOCUMENTATION_ELEMENT_COMPARATOR);
        MundoUtil.sortListMultimap(effects, DOCUMENTATION_ELEMENT_COMPARATOR);
        MundoUtil.sortListMultimap(expressions, DOCUMENTATION_ELEMENT_COMPARATOR);
        MundoUtil.sortListMultimap(events, DOCUMENTATION_ELEMENT_COMPARATOR);
        MundoUtil.sortListMultimap(types, DOCUMENTATION_ELEMENT_COMPARATOR);
        built = true;
    }

    public static void displayAll(ListMultimap<String, ? extends DocumentationElement> docElemListMultimap, CommandSender sender, int page) {
        int lastElemIndex = page * ELEMENTS_PER_PAGE;
        int lastPrecedingElemIndex = lastElemIndex - ELEMENTS_PER_PAGE;
        int prevElems = 0;
        for (String category : categories) {
            List<? extends DocumentationElement> docElems = docElemListMultimap.get(category);
            if (prevElems + docElems.size() > lastPrecedingElemIndex) {
                int skippedElems = Math.max(0, lastPrecedingElemIndex - prevElems);
                prevElems += skippedElems;
                for (; skippedElems < docElems.size(); skippedElems++) {
                    docElems.get(skippedElems).display(sender);
                    if (++prevElems == lastElemIndex) {
                        return;
                    }
                }
            } else {
                prevElems += docElems.size();
            }
        }
    }

    public static void displayCategory(ListMultimap<String, ? extends DocumentationElement> docElemListMultimap, CommandSender sender, String category, int page) {
        List<? extends DocumentationElement> docElems = docElemListMultimap.get(category);
        int max = page * ELEMENTS_PER_PAGE;
        int min = max - ELEMENTS_PER_PAGE;
        max = Math.min(max, docElems.size());
        for (int i = min; i < max; i++) {
            docElems.get(i).display(sender);
        }
    }
}
