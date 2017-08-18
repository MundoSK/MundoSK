package com.pie.tlatoani.Util;

/**
 * Created by Tlatoani on 8/17/17.
 */
public class Documentation {
    private final String name;
    private final String[] syntaxes;
    private final String originVersion;
    private final String[] requiredPlugins;
    private final String[][] examples;

    private Documentation(String name, String[] syntaxes, String originVersion, String[] requiredPlugins, String[][] examples) {
        this.name = name;
        this.syntaxes = syntaxes;
        this.originVersion = originVersion;
        this.requiredPlugins = requiredPlugins;
        this.examples = examples;
    }

    public static class Effect extends Documentation {

        public Effect(String name, String[] syntaxes, String originVersion, String[] requiredPlugins, String[][] examples) {
            super(name, syntaxes, originVersion, requiredPlugins, examples);
        }
    }

    public static class Expression extends Documentation {
        private final Class type;

        private Expression(String name, String[] syntaxes, String originVersion, String[] requiredPlugins, String[][] examples, Class type) {
            super(name, syntaxes, originVersion, requiredPlugins, examples);
            this.type = type;
        }
    }

    public static class Event extends Documentation {

        private Event(String name, String[] syntaxes, String originVersion, String[] requiredPlugins, String[][] examples) {
            super(name, syntaxes, originVersion, requiredPlugins, examples);
        }
    }
}
