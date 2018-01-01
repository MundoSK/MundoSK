package com.pie.tlatoani.Registration;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import com.pie.tlatoani.Util.Logging;
import org.bukkit.event.Cancellable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tlatoani on 8/21/17.
 */
public interface DocumentationBuilder<D extends DocumentationElement, B extends DocumentationBuilder<D, B>> {

    D build();

    B document(String name, String originVersion, String... description);

    B requiredPlugins(String... plugins);

    abstract class Abstract<D extends DocumentationElement, B extends Abstract<D, B>> implements DocumentationBuilder<D, B> {
        protected String name = null;
        protected String category = null;
        protected String[] syntaxes = null;
        protected String[] description = null;
        protected String originVersion = null;
        protected String[] requiredPlugins = null;

        Abstract(String category, String[] syntaxes) {
            this.category = category;
            this.syntaxes = syntaxes;
        }

        public B document(String name, String originVersion, String... description) {
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
        private List<Changer> changerBuilders = new ArrayList<>();

        public Expression(String category, String[] syntaxes, Class returnType, Class<? extends ch.njol.skript.lang.Expression> exprClass) {
            super(category, syntaxes);
            this.returnType = Classes.getExactClassInfo(returnType);
            if (exprClass != null) {
                addChangers(exprClass);
            }
        }

        private void addChangers(Class<? extends ch.njol.skript.lang.Expression> exprClass) {
            try {
                ch.njol.skript.lang.Expression expr = exprClass.newInstance();
                for (ch.njol.skript.classes.Changer.ChangeMode mode  : ch.njol.skript.classes.Changer.ChangeMode.values()) {
                    Class<?>[] changeTypes = expr.acceptChange(mode);
                    for (Class<?> changeType : changeTypes) {
                        changerBuilders.add(new Changer(mode, changeType, originVersion, ""));
                    }
                }
            } catch (Exception e) {
                Logging.reportException(this, e);
            }
        }

        private int changerIndex(ch.njol.skript.classes.Changer.ChangeMode mode, Class type) {
            int i = 0;
            for (Changer changer : changerBuilders) {
                if (changer.mode == mode && changer.type == type) {
                    return i;
                }
                i++;
            }
            return -1;
        }

        @Override
        public DocumentationElement.Expression build() {
            return new DocumentationElement.Expression(name, category, syntaxes, description, originVersion, returnType, requiredPlugins, changerBuilders);
        }

        public DocumentationBuilder.Expression changer(ch.njol.skript.classes.Changer.ChangeMode mode, Class type, String originVersion, String description) {
            int index = changerIndex(mode, type);
            Changer changer = new Changer(mode, type, originVersion, description);
            if (index == -1) {
                changerBuilders.add(changer);
            } else {
                changerBuilders.set(index, changer);
            }
            return this;
        }
    }

    class Changer {
        private ch.njol.skript.classes.Changer.ChangeMode mode;
        private Class type;
        private String description = null;
        private String originVersion = null;

        public Changer(ch.njol.skript.classes.Changer.ChangeMode mode, Class type, String originVersion, String description) {
            this.type = type;
            this.description = description;
            this.originVersion = originVersion;
        }

        public DocumentationElement.Changer build(DocumentationElement.Expression parent) {
            ClassInfo classInfo;
            boolean single;
            if (type.getComponentType() != null) {
                classInfo = Classes.getExactClassInfo(type.getComponentType());
                single = false;
            } else {
                classInfo = Classes.getExactClassInfo(type);
                single = true;
            }
            return new DocumentationElement.Changer(parent, mode, classInfo, single, description, originVersion);
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
            return new DocumentationElement.Event(name, category, syntaxes, description, originVersion, requiredPlugins, Cancellable.class.isAssignableFrom(event), eventValueBuilders);
        }

        public DocumentationBuilder.Event eventValue(Class type, String originVersion, String description) {
            eventValueBuilders.add(new EventValue(type, originVersion, description));
            return this;
        }
    }

    class EventValue {
        private Class type;
        private String description = null;
        private String originVersion = null;

        public EventValue(Class type, String originVersion, String description) {
            this.type = type;
            this.description = description;
            this.originVersion = originVersion;
        }

        public DocumentationElement.EventValue build(DocumentationElement.Event parent) {
            return new DocumentationElement.EventValue(parent, Classes.getExactClassInfo(type), description, originVersion);
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
