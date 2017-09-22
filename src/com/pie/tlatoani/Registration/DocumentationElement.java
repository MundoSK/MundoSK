package com.pie.tlatoani.Registration;

import org.bukkit.command.CommandSender;

/**
 * Created by Tlatoani on 8/17/17.
 */
public abstract class DocumentationElement {
    public final String name;
    public final String category;
    public final String[] syntaxes;
    public final String description;
    public final String originVersion;
    public final String[] requiredPlugins;

    public abstract void display(CommandSender sender);

    private DocumentationElement(String name, String category, String[] syntaxes, String description, String originVersion, String[] requiredPlugins) {
        this.name = name;
        this.category = category;
        this.syntaxes = syntaxes;
        this.description = description;
        this.originVersion = originVersion;
        this.requiredPlugins = requiredPlugins;
    }

    public static class Effect extends DocumentationElement {

        @Override
        public void display(CommandSender sender) {

        }

        public Effect(String name, String category, String[] syntaxes, String description, String originVersion, String[] requiredPlugins) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins);
        }
    }

    public static class Expression extends DocumentationElement {
        public final Class type;

        public Expression(String name, String category, String[] syntaxes, String description, String originVersion, Class type, String[] requiredPlugins) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins);
            this.type = type;
        }

        @Override
        public void display(CommandSender sender) {

        }
    }

    public static class Event extends DocumentationElement {

        @Override
        public void display(CommandSender sender) {

        }

        public Event(String name, String category, String[] syntaxes, String description, String originVersion, String[] requiredPlugins) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins);
        }
    }

    public static class Type extends DocumentationElement {
        public final String[] usages;

        public Type(String name, String category, String[] syntaxes, String[] usages, String description, String originVersion, String[] requiredPlugins) {
            super(name, category, syntaxes, description, originVersion, requiredPlugins);
            this.usages = usages;
        }

        @Override
        public void display(CommandSender sender) {

        }
    }
}
