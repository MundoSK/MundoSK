package com.pie.tlatoani.Core.Registration;

import ch.njol.skript.classes.ClassInfo;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tlatoani on 9/9/17.
 */
public class MundoClassInfo<T> extends ClassInfo<T> implements DocumentationBuilder<DocumentationElement.Type, MundoClassInfo<T>> {
    protected String name = null;
    protected String category = null;
    protected String[] syntaxes = null;
    protected String[] description = null;
    protected String originVersion = null;
    protected String[] requiredPlugins = null;
    protected List<String[]> examples = new LinkedList<>();

    public MundoClassInfo(Class<T> c, String[] names, String category) {
        super(c, names[0]);
        this.syntaxes = names;
        this.category = category;
        user(names);
        name(names[0]);
    }

    @Override
    public DocumentationElement.Type build() {
        return new DocumentationElement.Type(name, category, syntaxes, new String[0], description, originVersion, requiredPlugins, examples);
    }

    @Override
    public MundoClassInfo<T> document(String name, String originVersion, String... description) {
        Documentation.addBuilder(this);
        this.name = name;
        this.description = description;
        this.originVersion = originVersion;
        return this;
    }

    public MundoClassInfo<T> requiredPlugins(String... plugins) {
        requiredPlugins = plugins;
        return this;
    }

    public MundoClassInfo<T> example(String... example) {
        examples.add(example);
        return this;
    }

    @Override
    public String getDocName() {
        return name;
    }
}
