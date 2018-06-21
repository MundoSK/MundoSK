package com.pie.tlatoani.Core.Registration;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Pair;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Cancellable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tlatoani on 8/21/17.
 */
public interface DocumentationBuilder<D extends DocumentationElement, B extends DocumentationBuilder<D, B>> {

    D build();

    B document(String name, String originVersion, String... description);

    B requiredPlugins(String... plugins);

    B example(String... example);

    abstract class Abstract<D extends DocumentationElement, B extends Abstract<D, B>> implements DocumentationBuilder<D, B> {
        protected String name = null;
        protected String category = null;
        protected String[] syntaxes = null;
        protected String[] description = null;
        protected String originVersion = null;
        protected String[] requiredPlugins = null;
        protected List<String[]> examples = new LinkedList<>();

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

        public B example(String... example) {
            examples.add(example);
            return (B) this;
        }
    }

    abstract class Changeable<D extends DocumentationElement, B extends Changeable<D, B>> extends Abstract<D, B> {
        protected Class<? extends ch.njol.skript.lang.Expression> exprClass;
        protected List<Changer> changerBuilders = new ArrayList<>();

        Changeable(String category, String[] syntaxes, Class<? extends ch.njol.skript.lang.Expression> exprClass) {
            super(category, syntaxes);
            this.exprClass = exprClass;
        }

        protected void addChangers(Class<? extends ch.njol.skript.lang.Expression> exprClass) {
            try {
                ch.njol.skript.lang.Expression expr = exprClass.newInstance();
                for (ch.njol.skript.classes.Changer.ChangeMode mode  : ch.njol.skript.classes.Changer.ChangeMode.values()) {
                    Class<?>[] changeTypes = expr.acceptChange(mode);
                    if (changeTypes == null) {
                        continue;
                    }
                    if (mode == ch.njol.skript.classes.Changer.ChangeMode.RESET || mode == ch.njol.skript.classes.Changer.ChangeMode.DELETE) {
                        if (!containsChanger(mode, null)) {
                            changerBuilders.add(new Changer(mode, null, originVersion, ""));
                        }
                        continue;
                    }
                    for (Class<?> changeType : changeTypes) {
                        if (changeType != null && !containsChanger(mode, changeType)) {
                            changerBuilders.add(new Changer(mode, changeType, originVersion, ""));
                        }
                    }
                }
            } catch (Exception e) {
                Logging.debug(this, e);
            }
        }

        protected boolean containsChanger(ch.njol.skript.classes.Changer.ChangeMode mode, Class type) {
            for (Changer changer : changerBuilders) {
                if (changer.mode == mode && changer.type == type) {
                    return true;
                }
            }
            return false;
        }

        public B changer(ch.njol.skript.classes.Changer.ChangeMode mode, Class type, String originVersion, String description) {
            if (mode == ch.njol.skript.classes.Changer.ChangeMode.RESET || mode == ch.njol.skript.classes.Changer.ChangeMode.DELETE) {
                throw new IllegalArgumentException("Illegal ChangeMode: " + mode);
            }
            changerBuilders.add(new Changer(mode, type, originVersion, description));
            return (B) this;
        }

        public B changer(ch.njol.skript.classes.Changer.ChangeMode mode, String originVersion, String description) {
            if (mode != ch.njol.skript.classes.Changer.ChangeMode.RESET && mode != ch.njol.skript.classes.Changer.ChangeMode.DELETE) {
                throw new IllegalArgumentException("Illegal ChangeMode: " + mode);
            }
            changerBuilders.add(new Changer(mode, null, originVersion, description));
            return (B) this;
        }

        public B document(String name, String originVersion, String... description) {
            super.document(name, originVersion, description);
            return (B) this;
        }
    }

    static Effect effect(String category, String[] syntaxes) {
        return new Effect(category, syntaxes);
    }

    class Effect extends Abstract<DocumentationElement.Effect, Effect> {

        public Effect(String category, String[] syntaxes) {
            super(category, syntaxes);
        }

        @Override
        public DocumentationElement.Effect build() {
            return new DocumentationElement.Effect(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
        }
    }

    static Condition condition(String category, String[] syntaxes) {
        return new Condition(category, syntaxes, null);
    }

    class Condition extends Changeable<DocumentationElement.Condition, Condition> {

        public Condition(String category, String[] syntaxes, Class<? extends ch.njol.skript.lang.Expression> exprClass) {
            super(category, syntaxes, exprClass);
        }

        @Override
        public DocumentationElement.Condition build() {
            if (exprClass != null) {
                addChangers(exprClass);
            }
            return new DocumentationElement.Condition(name, category, syntaxes, description, originVersion, requiredPlugins, examples, changerBuilders);
        }
    }

    static Expression expression(String category, String[] syntaxes, Class returnType) {
        return new Expression(category, syntaxes, returnType, null);
    }

    class Expression extends Changeable<DocumentationElement.Expression, Expression> {
        private ClassInfo returnType;

        public Expression(String category, String[] syntaxes, Class returnType, Class<? extends ch.njol.skript.lang.Expression> exprClass) {
            super(category, syntaxes, exprClass);
            this.returnType = Classes.getExactClassInfo(returnType);
        }

        @Override
        public DocumentationElement.Expression build() {
            if (exprClass != null) {
                addChangers(exprClass);
            }
            return new DocumentationElement.Expression(name, category, syntaxes, description, originVersion, returnType, requiredPlugins, examples, changerBuilders);
        }
    }

    class Changer {
        private ch.njol.skript.classes.Changer.ChangeMode mode;
        private Class type;
        private String description = null;
        private String originVersion = null;

        public Changer(ch.njol.skript.classes.Changer.ChangeMode mode, Class type, String originVersion, String description) {
            this.mode = mode;
            this.type = type;
            this.description = description;
            this.originVersion = originVersion;
        }

        public DocumentationElement.Changer build(DocumentationElement parent) {
            ClassInfo classInfo;
            boolean single;
            if (type == null) {
                classInfo = null;
                single = false;
            } else if (type.getComponentType() != null) {
                classInfo = Classes.getExactClassInfo(type.getComponentType());
                single = false;
            } else {
                classInfo = Classes.getExactClassInfo(type);
                single = true;
            }
            Optional<Pair<ClassInfo, Boolean>> typeDoc = classInfo == null ? Optional.empty() : Optional.of(new Pair<>(classInfo, single));
            return new DocumentationElement.Changer(parent, mode, typeDoc, description, originVersion);
        }
    }

    static Event event(String category, String[] syntaxes, boolean cancellable) {
        return new Event(category, syntaxes, cancellable);
    }

    class Event extends Abstract<DocumentationElement.Event, Event> {
        private boolean cancellable;
        private List<EventValue> eventValueBuilders = new LinkedList<>();

        public Event(String category, String[] syntaxes, boolean cancellable) {
            super(category, syntaxes);
            this.cancellable = cancellable;
        }

        public Event(String category, String[] syntaxes, Class<? extends org.bukkit.event.Event> event) {
            this(category, syntaxes, Cancellable.class.isAssignableFrom(event));
        }

        @Override
        public DocumentationElement.Event build() {
            return new DocumentationElement.Event(name, category, syntaxes, description, originVersion, requiredPlugins, examples, cancellable, eventValueBuilders);
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

    static Scope scope(String category, String[] syntaxes) {
        return new Scope(category, syntaxes);
    }

    class Scope extends Abstract<DocumentationElement.Scope, Scope> {

        public Scope(String category, String[] syntaxes) {
            super(category, syntaxes);
        }

        @Override
        public DocumentationElement.Scope build() {
            return new DocumentationElement.Scope(name, category, syntaxes, description, originVersion, requiredPlugins, examples);
        }
    }

}
