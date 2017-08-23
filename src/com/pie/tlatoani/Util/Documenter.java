package com.pie.tlatoani.Util;

/**
 * Created by Tlatoani on 8/21/17.
 */
public interface Documenter {

    Documentation getDocumentation();

    void document(String name, String description, String originVersion);

    default void plugins(String... plugins) {
        getDocumentation().requiredPlugins = plugins;
    }

    class Effect implements Documenter {
        private final String[] syntaxes;
        private Documentation documentation = null;

        public Effect(String[] syntaxes) {
            this.syntaxes = syntaxes;
        }

        public void document(String name, String description, String originVersion) {
            documentation = new Documentation.Effect(name, syntaxes, description, originVersion);
            documentation.register();
        }

        @Override
        public Documentation getDocumentation() {
            return documentation;
        }
    }

    class Expression implements Documenter {
        private final String[] syntaxes;
        private final Class returnType;
        private Documentation documentation = null;

        public Expression(String[] syntaxes, Class returnType) {
            this.syntaxes = syntaxes;
            this.returnType = returnType;
        }

        @Override
        public Documentation getDocumentation() {
            return documentation;
        }

        @Override
        public void document(String name, String description, String originVersion) {
            documentation = new Documentation.Expression(name, syntaxes, returnType, description, originVersion);
            documentation.register();
        }
    }

    class Event implements Documenter {
        private final String[] syntaxes;
        private Documentation documentation = null;

        public Event(String[] syntaxes) {
            this.syntaxes = syntaxes;
        }

        public void document(String name, String description, String originVersion) {
            documentation = new Documentation.Event(name, syntaxes, description, originVersion);
            documentation.register();
        }

        @Override
        public Documentation getDocumentation() {
            return documentation;
        }
    }
}
