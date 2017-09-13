package com.pie.tlatoani.Registration;

/**
 * Created by Tlatoani on 8/17/17.
 */
public class DocumentationElement {
    public final String name;
    public final String category;
    public final String[] syntaxes;
    public final String description;
    public final String originVersion;
    public final String[] requiredPlugins;

    private DocumentationElement(String name, String category, String[] syntaxes, String description, String originVersion, String[] requiredPlugins) {
        this.name = name;
        this.category = category;
        this.syntaxes = syntaxes;
        this.description = description;
        this.originVersion = originVersion;
        this.requiredPlugins = requiredPlugins;
    }

    public static class Effect extends DocumentationElement {

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
    }

    public static class Event extends DocumentationElement {

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
    }
}
