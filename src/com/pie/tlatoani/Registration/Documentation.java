package com.pie.tlatoani.Registration;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.ImmutableGroupedList;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

import java.util.*;

/**
 * Created by Tlatoani on 9/9/17.
 */
public final class Documentation {
    public static final Comparator<DocumentationElement> DOCUMENTATION_ELEMENT_COMPARATOR = Comparator.comparing(docElem -> docElem.name.toLowerCase());
    public static final int ELEMENTS_PER_PAGE = 12;

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

    public static void accessDocumentation(CommandSender sender, String[] args) {
        if (listDocumentation(sender, args)) {
            return;
        }
        String docElemName = String.join(" ", args).substring(args[0].length() + 1).toLowerCase();
        Logging.debug(Documentation.class, "Searching for a DocElem named '" + docElemName + "'");
        for (List<DocumentationElement> docElems : allElements.getAllGroups()) {
            Logging.debug(Documentation.class, "Searching through " + docElems);
            Optional<DocumentationElement> docElemOptional = MundoUtil.binarySearchCeiling(docElems, docElemName, (name, docElem) -> name.compareToIgnoreCase(docElem.name));
            if (docElemOptional.filter(docElem -> docElem.name.toLowerCase().startsWith(docElemName)).isPresent()) {
                docElemOptional.get().display(sender);
                return;
            }
        }
        sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Invalid command. Do " + Mundo.ALT_CHAT_COLOR + "/mundosk doc help" + Mundo.PRIMARY_CHAT_COLOR + " for help");
    }

    private static boolean listDocumentation(CommandSender sender, String[] args) {
        if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
            //if (args.length == 2) { //Currently help is given whether or not additional arguments (which are unnecessary and meaningless) are specified
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "MundoSK Documentation Command Help");
                sender.sendMessage(Mundo.formatCommandDescription("doc[s] [help]", "Prints this list of commands"));
                sender.sendMessage(Mundo.formatCommandDescription("doc[s] cat[[egorie]s]", "Prints a list of the documentation categories"));
                sender.sendMessage(Mundo.formatCommandDescription("doc[s] all [page]", "Lists a page of all fullSyntax elements"));
                sender.sendMessage(Mundo.formatCommandDescription("doc[s] <elem type> [page]", "Lists a page of all fullSyntax elements of a certain type"));
                sender.sendMessage(Mundo.formatCommandDescription("doc[s] <category> [elem type] [page]", "Lists a page of fullSyntax elements in that category, either all of them or of a specific type"));
                sender.sendMessage(Mundo.formatCommandDescription("doc[s] <elem name>", "Lists the documentation for a specific fullSyntax element"));
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Accepted Element Types: " + Mundo.ALT_CHAT_COLOR + "Effect Condition Expression Event Type Scope");
                return true;
            //} else {
            //    return false;
            //}
        }
        if (args[1].equalsIgnoreCase("cat") || args[1].equalsIgnoreCase("cats") || args[1].equalsIgnoreCase("categories")) {
            if (args.length > 2) {
                return false;
            }
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Documentation Categories");
            for (String category : categories) {
                sender.sendMessage(Mundo.ALT_CHAT_COLOR + category);
            }
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            if (args.length == 2) {
                displayElems(sender, allElements, "All Syntax Elements", 1, true, true);
                return true;
            } else if (args.length == 3) {
                Optional<Integer> pageOptional = MundoUtil.parseIntOptional(args[2]);
                if (pageOptional.isPresent()) {
                    displayElems(sender, allElements, "All Syntax Elements", pageOptional.get(), true, true);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        Optional<ImmutableGroupedList<? extends DocumentationElement, String>> docElemGroupedListOptional = getDocElemGroupedList(args[1]);
        if (docElemGroupedListOptional.isPresent()) {
            ImmutableGroupedList<? extends DocumentationElement, String> docElemGroupedList = docElemGroupedListOptional.get();
            if (args.length == 2) {
                displayElems(sender, docElemGroupedList, "All " + MundoUtil.capitalize(args[1]), 1, true, false);
                return true;
            } else if (args.length == 3) {
                Optional<Integer> pageOptional = MundoUtil.parseIntOptional(args[2]);
                if (pageOptional.isPresent()) {
                    displayElems(sender, docElemGroupedList, "All " + MundoUtil.capitalize(args[1]), pageOptional.get(), true, false);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        Optional<String> categoryOptional = MundoUtil.binarySearchList(categories, args[1].toLowerCase(), (s, s2) -> s.compareTo(s2.toLowerCase()));
        if (categoryOptional.isPresent()) {
            String category = categoryOptional.get();
            if (args.length == 2) {
                displayElems(sender, allElements.getGroup(category), category + " Syntax Elements", 1, false, true);
                return true;
            } else if (args.length == 3) {
                Optional<Integer> pageOptional = MundoUtil.parseIntOptional(args[2]);
                if (pageOptional.isPresent()) {
                    displayElems(sender, allElements.getGroup(category), category + " Syntax Elements", pageOptional.get(), false, true);
                    return true;
                }
            } else if (args.length > 4) {
                return false;
            }
            int page;
            if (args.length == 4) {
                Optional<Integer> pageOptional = MundoUtil.parseIntOptional(args[3]);
                if (pageOptional.isPresent()) {
                    page = pageOptional.get();
                } else {
                    return false;
                }
            } else {
                page = 1;
            }
            return MundoUtil.mapOptional(getDocElemGroupedList(args[2]), docElemMultimap -> {
                displayElems(sender, docElemMultimap.getGroup(category), category + " " + MundoUtil.capitalize(args[2]) + "s", page, false, false);
                return true;
            }, () -> false);
        }
        return false;
    }

    private static Optional<ImmutableGroupedList<? extends DocumentationElement, String>> getDocElemGroupedList(String elemType) {
        if (Character.toLowerCase(elemType.charAt(elemType.length() - 1)) == 's') {
            elemType = elemType.substring(0, elemType.length() - 1);
        }
        switch (elemType.toLowerCase()) {
            case "effect": return Optional.of(effects);
            case "condition": return Optional.of(conditions);
            case "expression": return Optional.of(expressions);
            case "event": return Optional.of(events);
            case "type": return Optional.of(types);
            case "scope": return Optional.of(scopes);
            default: return Optional.empty();
        }
    }

    private static void displayElems(
            CommandSender sender,
            List<? extends DocumentationElement> docElems,
            String header,
            int page,
            boolean displayCategory,
            boolean displayType
    ) {
        int pages = 1 + ((docElems.size() - 1) / ELEMENTS_PER_PAGE);
        if (page > pages || page < 1) {
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Invalid page number " + Mundo.ALT_CHAT_COLOR + page + Mundo.PRIMARY_CHAT_COLOR + ", there are " + Mundo.ALT_CHAT_COLOR + pages + Mundo.PRIMARY_CHAT_COLOR + " pages of " + header);
            return;
        }
        sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Page " + page + " of " + pages + " of " + header);
        int max = page * ELEMENTS_PER_PAGE;
        int min = max - ELEMENTS_PER_PAGE;
        max = Math.min(max, docElems.size());
        for (int i = min; i < max; i++) {
            DocumentationElement docElem = docElems.get(i);
            sender.sendMessage(Mundo.TRI_CHAT_COLOR + (displayCategory ? docElem.category + " " : "") + (displayType ? docElem.getType() + " " : "") + Mundo.ALT_CHAT_COLOR + docElem.name);
        }
    }
}
