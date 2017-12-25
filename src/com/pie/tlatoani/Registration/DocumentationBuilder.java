package com.pie.tlatoani.Registration;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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

        public B document(String name, String originVersion, String description) {
            Documentation.addBuilder(this);
            this.name = name;
            this.description = description;
            this.originVersion = originVersion;
            this.requiredPlugins = new String[0];
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

    class Condition extends Abstract<DocumentationElement.Condition, Condition> {

        public Condition(String category, String[] syntaxes) {
            super(category, syntaxes);
        }

        @Override
        public DocumentationElement.Condition build() {
            return new DocumentationElement.Condition(name, category, syntaxes, description, originVersion, requiredPlugins);
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
        public final Class<? extends org.bukkit.event.Event> event;
        private Collection<EventValue> eventValueBuilders = new LinkedList<>();

        public Event(String category, String[] syntaxes, Class<? extends org.bukkit.event.Event> event) {
            super(category, syntaxes);
            this.event = event;
        }

        @Override
        public DocumentationElement.Event build() {
            return new DocumentationElement.Event(name, category, syntaxes, description, originVersion, requiredPlugins, eventValueBuilders);
        }

        public DocumentationBuilder.Event eventValue(Class type, String originVersion, String description) {
            eventValueBuilders.add(new EventValue(type, originVersion, description));
            return this;
        }
    }

    class EventValue {
        private ClassInfo type;
        private String description = null;
        private String originVersion = null;

        public EventValue(Class type, String originVersion, String description) {
            this.type = Classes.getExactClassInfo(type);
            this.description = description;
            this.originVersion = originVersion;
        }

        public DocumentationElement.EventValue build(DocumentationElement.Event parent) {
            return new DocumentationElement.EventValue(parent, type, description, originVersion);
        }
    }

    class Scope extends Abstract<DocumentationElement.Scope, Scope> {

        public Scope(String category, String[] syntaxes) {
            super(category, syntaxes);
        }

        @Override
        public DocumentationElement.Scope build() {
            return new DocumentationElement.Scope(name, category, syntaxes, description, originVersion, requiredPlugins);
        }
    }

}
