package com.pie.tlatoani.Registration;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tlatoani on 9/9/17.
 */
public final class Documentation {
    private static List<DocumentationBuilder> builders = new ArrayList<>();
    private static boolean built = false;

    private static Multimap<String, DocumentationElement.Effect> effects = null;
    private static Multimap<String, DocumentationElement.Expression> expressions = null;
    private static Multimap<String, DocumentationElement.Event> events = null;
    private static Multimap<String, DocumentationElement.Type> types = null;

    static void addBuilder(DocumentationBuilder builder) {
        builders.add(builder);
    }

    public static void buildDocumentation() {
        if (built) {
            throw new IllegalStateException("The documentation has already been built");
        }
        effects = ArrayListMultimap.create();
        expressions = ArrayListMultimap.create();
        events = ArrayListMultimap.create();
        types = ArrayListMultimap.create();
        for (DocumentationBuilder builder : builders) {
            DocumentationElement docElem = builder.build();
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
        built = true;
    }
}
