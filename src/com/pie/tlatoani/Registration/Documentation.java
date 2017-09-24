package com.pie.tlatoani.Registration;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.pie.tlatoani.Mundo;
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

    public static List<DocumentationElement> getAll(ListMultimap<String, ? extends DocumentationElement> docElemListMultimap, CommandSender sender, int page) {
        List<DocumentationElement> result = new ArrayList<>(ELEMENTS_PER_PAGE);
        int lastElemIndex = page * ELEMENTS_PER_PAGE;
        int lastPrecedingElemIndex = lastElemIndex - ELEMENTS_PER_PAGE;
        int prevElems = 0;
        for (String category : categories) {
            List<? extends DocumentationElement> docElems = docElemListMultimap.get(category);
            if (prevElems + docElems.size() > lastPrecedingElemIndex) {
                int skippedElems = Math.max(0, lastPrecedingElemIndex - prevElems);
                prevElems += skippedElems;
                for (; skippedElems < docElems.size(); skippedElems++) {
                    result.add(docElems.get(skippedElems));
                    if (++prevElems == lastElemIndex) {
                        return result;
                    }
                }
            } else {
                prevElems += docElems.size();
            }
        }
        return result;
    }

    public static List<DocumentationElement> getCategory(List<? extends DocumentationElement> docElems, CommandSender sender, int page) {
        List<DocumentationElement> result = new ArrayList<>();
        int max = page * ELEMENTS_PER_PAGE;
        int min = max - ELEMENTS_PER_PAGE;
        max = Math.min(max, docElems.size());
        for (int i = min; i < max; i++) {
            result.add(docElems.get(i));
        }
        return result;
    }

    public static void accessDocumentation(CommandSender sender, List<String> args) {
        if (args.size() == 0) {
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Documentation Categories");
            for (String category : categories) {
                sender.sendMessage(Mundo.ALT_CHAT_COLOR + category);
            }
            return;
        }
        int page;
        if (args.size() > 1) {
            try {
                page = Integer.parseInt(args.get(args.size() - 1));
                if (page < 1) {
                    page = 1;
                }
                args.remove(args.size() - 1);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
    }

    public static void displayElems(
            CommandSender sender,
            List<? extends DocumentationElement> docElems,
            String header,
            int page,
            int pages,
            boolean displayType
    ) {
        sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Page " + page + " of " + pages + " of " + header);
        for (DocumentationElement docElem : docElems) {
            sender.sendMessage((displayType ? Mundo.TRI_CHAT_COLOR + "" + docElem.getType() + "" : "") + Mundo.ALT_CHAT_COLOR + docElem.name);
        }
    }
}
