package com.pie.tlatoani.Registration;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;

/**
 * Created by Tlatoani on 8/21/17.
 */
public interface DocumentationBuilder<D extends DocumentationElement, B extends DocumentationBuilder<D, B>> {

    D build();

    B document(String name, String description, String originVersion);

    B requiredPlugins(String... plugins);

    abstract class Abstract<D extends DocumentationElement, B extends Abstract<D, B>> implements DocumentationBuilder<D, B> {
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

        public B document(String name, String description, String originVersion) {
            Documentation.addBuilder(this);
            this.name = name;
            this.description = description;
            this.originVersion = originVersion;
            return (B) this;
        }

        public B requiredPlugins(String... plugins) {
            requiredPlugins = plugins;
            return (B) this;
        }
    }

    class Effect extends Abstract<DocumentationElement.Effect, Effect> {

        public Effect(String category, String[] syntaxes) {
            super(category, syntaxes);
        }

        @Override
        public DocumentationElement.Effect build() {
            return new DocumentationElement.Effect(name, category, syntaxes, description, originVersion, requiredPlugins);
        }
    }

    class Expression extends Abstract<DocumentationElement.Expression, Expression> {
        private ClassInfo returnType;

        public Expression(String category, String[] syntaxes, Class returnType) {
            super(category, syntaxes);
            this.returnType = Classes.getExactClassInfo(returnType);
        }

        @Override
        public DocumentationElement.Expression build() {
            return new DocumentationElement.Expression(name, category, syntaxes, description, originVersion, returnType, requiredPlugins);
        }
    }

    class Event extends Abstract<DocumentationElement.Event, Event> {

        public Event(String category, String[] syntaxes) {
            super(category, syntaxes);
        }

        @Override
        public DocumentationElement.Event build() {
            return new DocumentationElement.Event(name, category, syntaxes, description, originVersion, requiredPlugins);
        }
    }

}
