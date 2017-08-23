package com.pie.tlatoani.Util;

/**
 * Created by Tlatoani on 8/21/17.
 */
public interface DocumentationBuilder<D extends Documentation> {

    D build();

    void document(String name, String description, String originVersion);

    void requiredPlugins(String... plugins);

    abstract class Abstract<D extends Documentation> implements DocumentationBuilder {
        protected String name = null;
        protected String category = null;
        protected String[] syntaxes = null;
        protected String description = null;
        protected String originVersion = null;
        protected String[] requiredPlugins = null;

        Abstract(String category, String[] syntaxes) {
            this.category = category;
            this.syntaxes = syntaxes;
        }

        public void document(String name, String description, String originVersion) {
            this.name = name;
            this.description = description;
            this.originVersion = originVersion;
        }

        public void requiredPlugins(String... plugins) {
            requiredPlugins = plugins;
        }
    }

    class Effect extends Abstract<Documentation.Effect> {

        public Effect(String category, String[] syntaxes) {
            super(category, syntaxes);
        }

        @Override
        public Documentation build() {
            return new Documentation.Effect(name, category, syntaxes, description, originVersion, requiredPlugins);
        }
    }

    class Expression extends Abstract<Documentation.Expression> {
        private Class returnType;

        public Expression(String category, String[] syntaxes, Class returnType) {
            super(category, syntaxes);
            this.returnType = returnType;
        }

        @Override
        public Documentation build() {
            return new Documentation.Expression(name, category, syntaxes, description, originVersion, returnType, requiredPlugins);
        }
    }
}
