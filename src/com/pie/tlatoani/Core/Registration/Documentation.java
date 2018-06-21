package com.pie.tlatoani.Core.Registration;

import com.pie.tlatoani.Util.Collections.ImmutableGroupedList;
import com.pie.tlatoani.Core.Static.Logging;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tlatoani on 9/9/17.
 */
public final class Documentation {
    public static final Comparator<String> WORD_BY_WORD_COMPARATOR = (s1, s2) -> {
        String[] words1 = s1.split(" ");
        String[] words2 = s2.split(" ");
        for (int i = 0;; i++) {
            boolean pastAllWords = true;
            for (int j = 0; j < Math.min(words1.length, words2.length); j++) {
                if (words1[j].length() > i) {
                    if (words2[j].length() > i) {
                        if (words1[j].charAt(i) == words2[j].charAt(i)) {
                            pastAllWords = false;
                        } else {
                            return words1[j].charAt(i) - words2[j].charAt(i);
                        }
                    } else {
                        return 1;
                    }
                } else {
                    if (words2[j].length() > i) {
                        return -1;
                    }
                }
            }
            if (pastAllWords) {
                break;
            }
        }
        return words2.length - words1.length;
    };
    public static final Comparator<DocumentationElement> DOCUMENTATION_ELEMENT_COMPARATOR = Comparator.comparing(docElem -> docElem.name.toLowerCase(), WORD_BY_WORD_COMPARATOR);

    private static List<DocumentationBuilder> builders = new LinkedList<>();
    private static boolean built = false;

    private static List<String> categories = null;
    private static ImmutableGroupedList<DocumentationElement, String> allElements = null;

    private static ImmutableGroupedList<DocumentationElement.Effect, String> effects = null;
    private static ImmutableGroupedList<DocumentationElement.Condition, String> conditions = null;
    private static ImmutableGroupedList<DocumentationElement.Expression, String> expressions = null;
    private static ImmutableGroupedList<DocumentationElement.Event, String> events = null;
    private static ImmutableGroupedList<DocumentationElement.Type, String> types = null;
    private static ImmutableGroupedList<DocumentationElement.Scope, String> scopes = null;

    static void addBuilder(DocumentationBuilder builder) {
        builders.add(builder);
    }

    public static void buildDocumentation() {
        if (built) {
            throw new IllegalStateException("The documentation has already been built");
        }
        ImmutableGroupedList.OrderedBuilder<DocumentationElement, String> allElementsBuilder = new ImmutableGroupedList.OrderedBuilder(DOCUMENTATION_ELEMENT_COMPARATOR, Comparator.<String>naturalOrder());
        ImmutableGroupedList.OrderedBuilder<DocumentationElement.Effect, String> effectsBuilder = new ImmutableGroupedList.OrderedBuilder(DOCUMENTATION_ELEMENT_COMPARATOR, Comparator.<String>naturalOrder());
        ImmutableGroupedList.OrderedBuilder<DocumentationElement.Expression, String> expressionsBuilder = new ImmutableGroupedList.OrderedBuilder(DOCUMENTATION_ELEMENT_COMPARATOR, Comparator.<String>naturalOrder());
        ImmutableGroupedList.OrderedBuilder<DocumentationElement.Condition, String> conditionsBuilder = new ImmutableGroupedList.OrderedBuilder(DOCUMENTATION_ELEMENT_COMPARATOR, Comparator.<String>naturalOrder());
        ImmutableGroupedList.OrderedBuilder<DocumentationElement.Event, String> eventsBuilder = new ImmutableGroupedList.OrderedBuilder(DOCUMENTATION_ELEMENT_COMPARATOR, Comparator.<String>naturalOrder());
        ImmutableGroupedList.OrderedBuilder<DocumentationElement.Type, String> typesBuilder = new ImmutableGroupedList.OrderedBuilder(DOCUMENTATION_ELEMENT_COMPARATOR, Comparator.<String>naturalOrder());
        ImmutableGroupedList.OrderedBuilder<DocumentationElement.Scope, String> scopesBuilder = new ImmutableGroupedList.OrderedBuilder(DOCUMENTATION_ELEMENT_COMPARATOR, Comparator.<String>naturalOrder());
        for (DocumentationBuilder builder : builders) {
            DocumentationElement docElem = builder.build();
            allElementsBuilder.add(docElem.category, docElem);
            if (docElem instanceof DocumentationElement.Effect) {
                effectsBuilder.add(docElem.category, (DocumentationElement.Effect) docElem);
            } else if (docElem instanceof DocumentationElement.Condition) {
                conditionsBuilder.add(docElem.category, (DocumentationElement.Condition) docElem);
            } else if (docElem instanceof DocumentationElement.Expression) {
                expressionsBuilder.add(docElem.category, (DocumentationElement.Expression) docElem);
            } else if (docElem instanceof DocumentationElement.Event) {
                eventsBuilder.add(docElem.category, (DocumentationElement.Event) docElem);
            } else if (docElem instanceof DocumentationElement.Type) {
                typesBuilder.add(docElem.category, (DocumentationElement.Type) docElem);
            } else if (docElem instanceof DocumentationElement.Scope) {
                scopesBuilder.add(docElem.category, (DocumentationElement.Scope) docElem);
            }
        }
        Documentation.allElements = allElementsBuilder.build();
        Documentation.effects = effectsBuilder.build();
        Documentation.conditions = conditionsBuilder.build();
        Documentation.expressions = expressionsBuilder.build();
        Documentation.events = eventsBuilder.build();
        Documentation.types = typesBuilder.build();
        Documentation.scopes = scopesBuilder.build();
        Documentation.categories = allElements.getGroupKeys();
        built = true;
        Logging.debug(Documentation.class, "All DocElems: " + allElements);
    }



    public static List<String> getCategories() {
        return categories;
    }

    public static ImmutableGroupedList<DocumentationElement, String> getAllElements() {
        return allElements;
    }

    public static ImmutableGroupedList<DocumentationElement.Effect, String> getEffects() {
        return effects;
    }

    public static ImmutableGroupedList<DocumentationElement.Condition, String> getConditions() {
        return conditions;
    }

    public static ImmutableGroupedList<DocumentationElement.Expression, String> getExpressions() {
        return expressions;
    }

    public static ImmutableGroupedList<DocumentationElement.Event, String> getEvents() {
        return events;
    }

    public static ImmutableGroupedList<DocumentationElement.Type, String> getTypes() {
        return types;
    }

    public static ImmutableGroupedList<DocumentationElement.Scope, String> getScopes() {
        return scopes;
    }
}
