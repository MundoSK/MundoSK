package com.pie.tlatoani.Registration;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.util.Pair;
import com.pie.tlatoani.Mundo;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Created by Tlatoani on 8/17/17.
 */
public abstract class DocumentationElement {
    public final String name;
    public final String category;
    public final String[] syntaxes;
    public final String[] description;
    public final String originVersion;
    public final String[] requiredPlugins;
    public final String[][] examples;

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
        sender.sendMessage(Mundo.formatMundoSKInfo(category + " " + getType(), name));
        sender.sendMessage(Mundo.formatMundoSKInfo("Since", "MundoSK " + originVersion));
        if (requiredPlugins.length > 0) {
            sender.sendMessage(Mundo.formatMundoSKInfo("Required Plugins", String.join(" ", requiredPlugins)));
        }
    }

    protected void displaySyntax(CommandSender sender) {
        if (syntaxes.length == 1) {
            sender.sendMessage(Mundo.formatMundoSKInfo("Syntax", syntaxes[0]));
        } else {
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Syntaxes");
            for (String syntax : syntaxes) {
                sender.sendMessage(Mundo.ALT_CHAT_COLOR + syntax);
            }
        }
    }

    protected void displayDesc(CommandSender sender) {
        if (description.length == 1) {
            sender.sendMessage(Mundo.formatMundoSKInfo("Description", description[0]));
        } else {
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Description");
            for (String descLine : description) {
                sender.sendMessage(Mundo.ALT_CHAT_COLOR + descLine);
            }
        }
    }

    protected void displayExamples(CommandSender sender) {
        for (int i = 1; i <= examples.length; i++) {
            String[] example = examples[i - 1];
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Example " + i);
            for (int line = 1; line <= example.length; line++) {
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "" + line + Mundo.ALT_CHAT_COLOR + example[line - 1]);
            }
        }
    }

    @Override
    public String toString() {
        return "DocumentationElement(" + category + " " + getType() + ": " + name + ")";
    }

    private DocumentationElement(String name, String category, String[] syntaxes, String description[], String originVersion, String[] requiredPlugins, String[][] examples) {
        this.name = name;
        this.category = category;
        this.syntaxes = Arrays.stream(syntaxes).map(syntax -> syntax.replaceAll("\\d+¦", "")).toArray(String[]::new); //Borrowed from Tuke_Nuke's TuSKe from the SsyntaxInfo class's fixPattern() method
        this.description = description;
        this.originVersion = originVersion;
        this.requiredPlugins = requiredPlugins;
        this.examples = examples;
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

        public Effect(String name, String category, String[] syntaxes, String description[], String originVersion, String[] requiredPlugins, String[][] examples) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
        }
    }

    public static class Condition extends DocumentationElement {
        public final Changer[] changers;

        public Condition(String name, String category, String[] syntaxes, String[] description, String originVersion, String[] requiredPlugins, String[][] examples, Collection<DocumentationBuilder.Changer> changerBuilders) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.changers = changerBuilders.stream().map(builder -> builder.build(this)).toArray(Changer[]::new);
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
            if (changers.length > 0) {
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Changers");
                for (Changer changer : changers) {
                    changer.display(sender);
                }
            }
            displayExamples(sender);
        }
    }

    public static class Expression extends DocumentationElement {
        public final ClassInfo type;
        public final Changer[] changers;

        public Expression(String name, String category, String[] syntaxes, String[] description, String originVersion, ClassInfo type, String[] requiredPlugins, String[][] examples, Collection<DocumentationBuilder.Changer> changerBuilders) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.type = type;
            this.changers = changerBuilders.stream().map(builder -> builder.build(this)).toArray(Changer[]::new);
        }

        @Override
        public DocumentationElement.ElementType getType() {
            return ElementType.EXPRESSION;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            sender.sendMessage(Mundo.formatMundoSKInfo("Type", type.getDocName()));
            displaySyntax(sender);
            displayDesc(sender);
            if (changers.length > 0) {
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Changers");
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
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + modeSyntax(mode) + typeSyntax + (originVersion.equals(parent.originVersion) ? "" : Mundo.TRI_CHAT_COLOR + " Since " + originVersion) + Mundo.ALT_CHAT_COLOR + " " + description);
        }
    }

    public static class Event extends DocumentationElement {
        public final boolean cancellable;
        public final EventValue[] eventValues;

        public Event(String name, String category, String[] syntaxes, String[] description, String originVersion, String[] requiredPlugins, String[][] examples, boolean cancellable, Collection<DocumentationBuilder.EventValue> eventValueBuilders) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.cancellable = cancellable;
            this.eventValues = eventValueBuilders.stream().map(builder -> builder.build(this)).toArray(EventValue[]::new);
        }

        @Override
        public DocumentationElement.ElementType getType() {
            return ElementType.EVENT;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            sender.sendMessage(Mundo.formatMundoSKInfo("Cancellable", cancellable ? "Yes" : "No"));
            displaySyntax(sender);
            displayDesc(sender);
            if (eventValues.length > 0) {
                sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "Event Values");
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
            sender.sendMessage(Mundo.PRIMARY_CHAT_COLOR + "event-" + type.getCodeName() + (originVersion.equals(parent.originVersion) ? "" : Mundo.TRI_CHAT_COLOR + " Since " + originVersion) + Mundo.ALT_CHAT_COLOR + " " + description);
        }
    }

    public static class Type extends DocumentationElement {
        public final String[] usages;

        public Type(String name, String category, String[] syntaxes, String[] usages, String[] description, String originVersion, String[] requiredPlugins, String[][] examples) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
            this.usages = usages;
        }

        @Override
        public ElementType getType() {
            return ElementType.TYPE;
        }

        @Override
        public void display(CommandSender sender) {
            displayHeader(sender);
            displaySyntax(sender);
            sender.sendMessage(Mundo.formatMundoSKInfo("Usages", syntaxes.length == 0 ? "Cannot be written in scripts" : String.join(", ", usages)));
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

        public Scope(String name, String category, String[] syntaxes, String description[], String originVersion, String[] requiredPlugins, String[][] examples) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
        }
    }
}
