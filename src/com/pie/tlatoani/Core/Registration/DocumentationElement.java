package com.pie.tlatoani.Core.Registration;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.util.Pair;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.Core.Static.MainCommand;
import com.pie.tlatoani.Util.Collections.ImmutableListCollector;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * Created by Tlatoani on 8/17/17.
 */
public abstract class DocumentationElement {
    public final String name;
    public final String category;
    public final ImmutableList<String> syntaxes;
    public final ImmutableList<String> description;
    public final String originVersion;
    public final ImmutableList<String> requiredPlugins;
    public final ImmutableList<ImmutableList<String>> examples;

    public enum ElementType {
        EFFECT("Effect"),
        CONDITION("Condition"),
        EXPRESSION("Expression"),
        EVENT("Event"),
        TYPE("Type"),
        SCOPE("Scope");

        public final String toString;

        ElementType(String toString) {
            this.toString = toString;
        }

        public String toString() {
            return toString;
        }
    }

    public abstract ElementType getType();

    public abstract void display(CommandSender sender);

    protected void displayHeader(CommandSender sender) {
        sender.sendMessage(MainCommand.formatMundoSKInfo(category + " " + getType(), name));
        sender.sendMessage(MainCommand.formatMundoSKInfo("Since", "MundoSK " + originVersion));
        if (requiredPlugins.size() > 0) {
            sender.sendMessage(MainCommand.formatMundoSKInfo("Required Plugins", String.join(" ", requiredPlugins)));
        }
    }

    protected void displaySyntax(CommandSender sender) {
        if (syntaxes.size() == 1) {
            sender.sendMessage(MainCommand.formatMundoSKInfo("Syntax", syntaxes.get(0)));
        } else {
            sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "Syntaxes");
            for (String syntax : syntaxes) {
                sender.sendMessage(MainCommand.ALT_CHAT_COLOR + syntax);
            }
        }
    }

    protected void displayDesc(CommandSender sender) {
        if (description.size() == 1) {
            sender.sendMessage(MainCommand.formatMundoSKInfo("Description", description.get(0)));
        } else {
            sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "Description");
            for (String descLine : description) {
                sender.sendMessage(MainCommand.ALT_CHAT_COLOR + descLine);
            }
        }
    }

    protected void displayExamples(CommandSender sender) {
        for (int i = 1; i <= examples.size(); i++) {
            ImmutableList<String> example = examples.get(i - 1);
            sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "Example " + i);
            for (int line = 1; line <= example.size(); line++) {
                sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "" + String.format(Locale.US, "%02d", line) + " " + MainCommand.ALT_CHAT_COLOR + example.get(line - 1));
            }
        }
    }

    @Override
    public String toString() {
        return "DocumentationElement(" + category + " " + getType() + ": " + name + ")";
    }

    private DocumentationElement(String name, String category, String[] syntaxes, String[] description, String originVersion, String[] requiredPlugins, List<String[]> examples) {
        this.name = name;
        this.category = category;
        this.syntaxes = Arrays
                .stream(syntaxes)
                .map(syntax -> syntax.replaceAll("\\d+¦", "")) //Borrowed from Tuke_Nuke's TuSKe from the SsyntaxInfo class's fixPattern() method
                .collect(new ImmutableListCollector<>());
        this.description = ImmutableList.copyOf(description);
        this.originVersion = originVersion;
        this.requiredPlugins = ImmutableList.copyOf(requiredPlugins);
        this.examples = examples
                .stream()
                .map(ImmutableList::copyOf)
                .collect(new ImmutableListCollector<>());
    }

    public static class Effect extends DocumentationElement {

        @Override
        public DocumentationElement.ElementType getType() {
            return ElementType.EFFECT;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            displaySyntax(sender);
            displayDesc(sender);
            displayExamples(sender);
        }

        public Effect(String name, String category, String[] syntaxes, String[] description, String originVersion, String[] requiredPlugins, List<String[]> examples) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
        }
    }

    public static class Condition extends DocumentationElement {
        public final ImmutableList<Changer> changers;

        public Condition(String name, String category, String[] syntaxes, String[] description, String originVersion, String[] requiredPlugins, List<String[]> examples, Collection<DocumentationBuilder.Changer> changerBuilders) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.changers = changerBuilders.stream().map(builder -> builder.build(this)).collect(new ImmutableListCollector<>());
        }

        @Override
        public DocumentationElement.ElementType getType() {
            return ElementType.CONDITION;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            displaySyntax(sender);
            displayDesc(sender);
            if (changers.size() > 0) {
                sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "Changers");
                for (Changer changer : changers) {
                    changer.display(sender);
                }
            }
            displayExamples(sender);
        }
    }

    public static class Expression extends DocumentationElement {
        public final ClassInfo type;
        public final ImmutableList<Changer> changers;

        public Expression(String name, String category, String[] syntaxes, String[] description, String originVersion, ClassInfo type, String[] requiredPlugins, List<String[]> examples, List<DocumentationBuilder.Changer> changerBuilders) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.type = type;
            this.changers = changerBuilders.stream().map(builder -> builder.build(this)).collect(new ImmutableListCollector<>());
        }

        @Override
        public DocumentationElement.ElementType getType() {
            return ElementType.EXPRESSION;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            sender.sendMessage(MainCommand.formatMundoSKInfo("Type", type.getDocName()));
            displaySyntax(sender);
            displayDesc(sender);
            if (changers.size() > 0) {
                sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "Changers");
                for (Changer changer : changers) {
                    changer.display(sender);
                }
            }
            displayExamples(sender);
        }
    }

    public static class Changer {
        public final DocumentationElement parent;
        public final ch.njol.skript.classes.Changer.ChangeMode mode;
        public final Optional<Pair<ClassInfo, Boolean>> type;
        public final String description;
        public final String originVersion;

        public Changer(DocumentationElement parent, ch.njol.skript.classes.Changer.ChangeMode mode, Optional<Pair<ClassInfo, Boolean>> type, String description, String originVersion) {
            this.parent = parent;
            this.mode = mode;
            this.type = type;
            this.description = description;
            this.originVersion = originVersion;
        }

        public static String modeSyntax(ch.njol.skript.classes.Changer.ChangeMode mode) {
            switch (mode) {
                case ADD:
                case REMOVE:
                case RESET:
                    return mode.name().toLowerCase();
                case SET:
                    return "set to";
                case DELETE:
                    return "clear/delete";
                case REMOVE_ALL:
                    return "remove all";
            }
            throw new IllegalArgumentException("Mode: " + mode);
        }

        public void display(CommandSender sender) {
            String typeSyntax = type.map(pair -> " " + pair.getFirst().getCodeName() + (pair.getSecond() ? "" : "s")).orElse("");
            sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + modeSyntax(mode) + typeSyntax + (originVersion.equals(parent.originVersion) ? "" : MainCommand.TRI_CHAT_COLOR + " Since " + originVersion) + MainCommand.ALT_CHAT_COLOR + " " + description);
        }
    }

    public static class Event extends DocumentationElement {
        public final boolean cancellable;
        public final ImmutableList<EventValue> eventValues;

        public Event(String name, String category, String[] syntaxes, String[] description, String originVersion, String[] requiredPlugins, List<String[]> examples, boolean cancellable, Collection<DocumentationBuilder.EventValue> eventValueBuilders) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.cancellable = cancellable;
            this.eventValues = eventValueBuilders.stream().map(builder -> builder.build(this)).collect(new ImmutableListCollector<>());
        }

        @Override
        public DocumentationElement.ElementType getType() {
            return ElementType.EVENT;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            sender.sendMessage(MainCommand.formatMundoSKInfo("Cancellable", cancellable ? "Yes" : "No"));
            displaySyntax(sender);
            displayDesc(sender);
            if (eventValues.size() > 0) {
                sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "Event Values");
                for (EventValue eventValue : eventValues) {
                    eventValue.display(sender);
                }
            }
            displayExamples(sender);
        }
    }

    public static class EventValue {
        public final Event parent;
        public final ClassInfo type;
        public final String description;
        public final String originVersion;

        public EventValue(Event parent, ClassInfo type, String description, String originVersion) {
            this.parent = parent;
            this.type = type;
            this.description = description;
            this.originVersion = originVersion;
        }

        public void display(CommandSender sender) {
            sender.sendMessage(MainCommand.PRIMARY_CHAT_COLOR + "event-" + type.getCodeName() + (originVersion.equals(parent.originVersion) ? "" : MainCommand.TRI_CHAT_COLOR + " Since " + originVersion) + MainCommand.ALT_CHAT_COLOR + " " + description);
        }
    }

    public static class Type extends DocumentationElement {
        public final ImmutableList<String> usages;

        public Type(String name, String category, String[] syntaxes, String[] usages, String[] description, String originVersion, String[] requiredPlugins, List<String[]> examples) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.usages = ImmutableList.copyOf(usages);
        }

        @Override
        public ElementType getType() {
            return ElementType.TYPE;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            displaySyntax(sender);
            sender.sendMessage(MainCommand.formatMundoSKInfo("Usages", usages.size() == 0 ? "Cannot be written in scripts" : String.join(", ", usages)));
            displayDesc(sender);
            displayExamples(sender);
        }
    }

    public static class Scope extends DocumentationElement {

        @Override
        public DocumentationElement.ElementType getType() {
            return ElementType.EFFECT;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            displaySyntax(sender);
            displayDesc(sender);
            displayExamples(sender);
        }

        public Scope(String name, String category, String[] syntaxes, String[] description, String originVersion, String[] requiredPlugins, List<String[]> examples) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
        }
    }
}
