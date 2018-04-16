package com.pie.tlatoani.Registration;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ParseContext;
import ch.njol.util.Pair;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Util.Static.Logging;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.*;

/**
 * Created by Tlatoani on 9/9/17.
 */
public class EnumClassInfo<E> extends MundoClassInfo<E> {
    private final List<Map.Entry<String, E>> pairings;
    private Object[] allValues = null;

    public EnumClassInfo(Class<E> c, String[] names, String category, E[] values) {
        super(c, names, category);
        pairings = new ArrayList<>(values.length);
        for (E value : values) {
            pair(value.toString(), value);
        }
        Logging.debug(this, "pie");
        afterInitialization();
    }

    public EnumClassInfo(Class<E> c, String[] names, String category, Map<String, E> valueMap) {
        super(c, names, category);
        this.pairings = new ArrayList<>(valueMap.entrySet());
        Logging.debug(this, "pie");
        afterInitialization();
    }

    private void afterInitialization() {
        Logging.debug(this, "afterInit");
        parser(new Parser<E>() {
            @Override
            public E parse(String s, ParseContext parseContext) {
                return EnumClassInfo.this.parse(s);
            }

            @Override
            public String toString(E e, int i) {
                return EnumClassInfo.this.toString(e);
            }

            @Override
            public String toVariableNameString(E e) {
                return EnumClassInfo.this.toString(e);
            }

            @Override
            public String getVariableNamePattern() {
                return ".+";
            }
        });
        serializer(new Serializer<E>() {
            @Override
            public Fields serialize(E e) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("value", EnumClassInfo.this.toString(e));
                return fields;
            }

            @Override
            public void deserialize(E e, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }

            @Override
            public E deserialize(Fields fields) throws StreamCorruptedException {
                return EnumClassInfo.this.parse((String) fields.getObject("value"));
            }
        });
    }

    public EnumClassInfo<E> pair(String name, E value) {
        pairings.add(0, new Pair<String, E>(name.toUpperCase(), value));
        return this;
    }

    @Override
    public DocumentationElement.Type build() {
        String[] usages = new String[pairings.size()];
        for (int i = 0; i < usages.length; i++) {
            usages[i] = pairings.get(i).getKey().toLowerCase();
        }
        return new DocumentationElement.Type(name, category, syntaxes, usages, description, originVersion, requiredPlugins, examples);
    }

    @Override
    public EnumClassInfo<E> requiredPlugins(String... requiredPlugins) {
        super.requiredPlugins(requiredPlugins);
        return this;
    }

    //

    public String getPluralCodeName() {
        if (getCodeName().endsWith("y")) {
            return getCodeName().substring(0,getCodeName().length() - 1) + "ies";
        } else {
            return getCodeName() + "s";
        }
    }

    public E parse(String s) {
        s = s.toUpperCase();
        for (Map.Entry<String, E> pairing : pairings) {
            if (s.equals(pairing.getKey())) {
                return pairing.getValue();
            }
        }
        return null;
    }

    public String toString(E e) {
        Logging.debug(this, "tostringing: " + e);
        for (Map.Entry<String, E> pairing : pairings) {
            if (e == pairing.getValue()) {
                return pairing.getKey().toLowerCase();
            }
        }
        return null;
    }

    public Object[] getAllValues() {
        if (allValues == null) {
            Set<E> allValuesSet = new HashSet<E>();
            for (Map.Entry<String, E> pairing : pairings) {
                allValuesSet.add(pairing.getValue());
            }
            allValues = allValuesSet.toArray();
        }
        return allValues;
    }
}
