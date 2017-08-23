package com.pie.tlatoani.Util;

/**
 * Created by Tlatoani on 8/17/17.
 */
public class Documentation {
    private final String name;
    private final String[] syntaxes;
    private final String description;
    private final String originVersion;
    String[] requiredPlugins = new String[0];

    public void register() {

    }

    private Documentation(String name, String[] syntaxes, String description, String originVersion) {
        this.name = name;
        this.syntaxes = syntaxes;
        this.description = description;
        this.originVersion = originVersion;
    }

    public static class Effect extends Documentation {

        public Effect(String name, String[] syntaxes, String description, String originVersion) {
            super(name, syntaxes, description, originVersion);
        }
    }

    public static class Expression extends Documentation {
        private final Class type;

        public Expression(String name, String[] syntaxes, Class type, String description, String originVersion) {
            super(name, syntaxes, description, originVersion);
            this.type = type;
        }
    }

    public static class Event extends Documentation {

        public Event(String name, String[] syntaxes, String description, String originVersion) {
            super(name, syntaxes, description, originVersion);
        }
    }

    public static class Type extends Documentation {
        private final String[] usages;

        public Type(String name, String[] syntaxes, String[] usages, String description, String originVersion) {
            super(name, syntaxes, description, originVersion);
            this.usages = usages;
        }
    }
}
