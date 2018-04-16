package com.pie.tlatoani.Registration;

import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.Collections.ImmutableGroupedList;
import com.pie.tlatoani.Util.Static.Logging;
import com.pie.tlatoani.Util.Static.MundoUtil;
import com.pie.tlatoani.Util.Static.OptionalUtil;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tlatoani on 1/1/18.
 */
public class DocumentationCommand {
    public static final int ELEMENTS_PER_PAGE = 12;

    public static void accessDocumentation(CommandSender sender, String[] args) {
        if (listDocumentation(sender, args)) {
            return;
        }
        String docElemName = String.join(" ", args).substring(args[0].length() + 1).toLowerCase();
        Logging.debug(Documentation.class, "Searching for a DocElem named '" + docElemName + "'");
        for (List<DocumentationElement> docElems : Documentation.getAllElements().getAllGroups()) {
            Logging.debug(Documentation.class, "Searching through " + docElems);
            Optional<DocumentationElement> docElemOptional = MundoUtil.binarySearchCeiling(docElems, docElemName, (name, docElem) -> Documentation.WORD_BY_WORD_COMPARATOR.compare(name, docElem.name.toLowerCase()));
            Logging.debug(DocumentationCommand.class, "Found docElem " + docElemOptional);
            if (docElemOptional.filter(docElem -> MundoUtil.wordsStartWith(docElem.name.toLowerCase(), docElemName)).isPresent()) {
                docElemOptional.get().display(sender);
                return;
            }
        }
        sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Invalid command. Do " + Mundo.ALT_CHAT_COLOR + "/mundosk doc help" + Mundo.PRIMARY_CHAT_COLOR + " for help");
    }

    private static boolean listDocumentation(CommandSender sender, String[] args) {
        if (args.length == 1 || args[1].equalsIgnoreCase("help")) {
            //Currently help is given whether or not additional arguments (which are unnecessary and meaningless) are specified
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "MundoSK Documentation Command Help");
            sender.sendMessage(Mundo.formatCommandDescription("doc[s] [help]", "Prints this list of commands"));
            sender.sendMessage(Mundo.formatCommandDescription("doc[s] cat[[egorie]s]", "Prints a list of the documentation categories"));
            sender.sendMessage(Mundo.formatCommandDescription("doc[s] all [page]", "Lists a page of all syntax elements"));
            sender.sendMessage(Mundo.formatCommandDescription("doc[s] <elem type> [page]", "Lists a page of all syntax elements of a certain type"));
            sender.sendMessage(Mundo.formatCommandDescription("doc[s] <category> [elem type] [page]", "Lists a page of syntax elements in that category, either all of them or of a specific type"));
            sender.sendMessage(Mundo.formatCommandDescription("doc[s] <elem name>", "Lists the documentation for a specific syntax element"));
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Accepted Element Types: " + Mundo.ALT_CHAT_COLOR + "Effect Condition Expression Event Type Scope");
            return true;
        }
        if (args[1].equalsIgnoreCase("generatefile")) {
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "The documentation file will now be generated. This command is not intended to be executed by people other than the developer. "
                    + "The file generated will probably not be suitable for use as documentation itself "
                    + "and is intended to be used to import documentation into online Skript documentation sites such as skUnity and Skript Hub. "
                    + "If you would like to view MundoSK's documentation, use the " + Mundo.ALT_CHAT_COLOR + "/mundosk doc" + Mundo.PRIMARY_CHAT_COLOR + " command or visit one of these websites.");
            try {
                DocumentationFileGenerator.generateDocumentationFile();
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "MundoSK has successfully generated the documentation file.");
            } catch (IOException e) {
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "An error occurred while generating the documentation file. See the console for details.");
                Logging.reportException(DocumentationCommand.class, e);
            }
            return true;
        }
        if (args[1].equalsIgnoreCase("cat") || args[1].equalsIgnoreCase("cats") || args[1].equalsIgnoreCase("categories")) {
            if (args.length > 2) {
                return false;
            }
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Documentation Categories");
            for (String category : Documentation.getCategories()) {
                sender.sendMessage(Mundo.ALT_CHAT_COLOR + category);
            }
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            if (args.length == 2) {
                displayElems(sender, Documentation.getAllElements(), "All Syntax Elements", 1, true, true);
                return true;
            } else if (args.length == 3) {
                Optional<Integer> pageOptional = MundoUtil.parseIntOptional(args[2]);
                if (pageOptional.isPresent()) {
                    displayElems(sender, Documentation.getAllElements(), "All Syntax Elements", pageOptional.get(), true, true);
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
        Optional<String> categoryOptional = MundoUtil.binarySearchList(Documentation.getCategories(), args[1].toLowerCase(), (s, s2) -> s.compareTo(s2.toLowerCase()));
        if (categoryOptional.isPresent()) {
            String category = categoryOptional.get();
            if (args.length == 2) {
                displayElems(sender, Documentation.getAllElements().getGroup(category), category + " Syntax Elements", 1, false, true);
                return true;
            } else if (args.length == 3) {
                Optional<Integer> pageOptional = MundoUtil.parseIntOptional(args[2]);
                if (pageOptional.isPresent()) {
                    displayElems(sender, Documentation.getAllElements().getGroup(category), category + " Syntax Elements", pageOptional.get(), false, true);
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
            return OptionalUtil.map(getDocElemGroupedList(args[2]), () -> false, docElemMultimap -> {
                displayElems(sender, docElemMultimap.getGroup(category), category + " " + MundoUtil.capitalize(args[2]) + "s", page, false, false);
                return true;
            });
        }
        return false;
    }

    private static Optional<ImmutableGroupedList<? extends DocumentationElement, String>> getDocElemGroupedList(String elemType) {
        if (Character.toLowerCase(elemType.charAt(elemType.length() - 1)) == 's') {
            elemType = elemType.substring(0, elemType.length() - 1);
        }
        switch (elemType.toLowerCase()) {
            case "effect": return Optional.of(Documentation.getEffects());
            case "condition": return Optional.of(Documentation.getConditions());
            case "expression": return Optional.of(Documentation.getExpressions());
            case "event": return Optional.of(Documentation.getEvents());
            case "type": return Optional.of(Documentation.getTypes());
            case "scope": return Optional.of(Documentation.getScopes());
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
