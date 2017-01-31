package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.StringMode;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import ch.njol.util.Pair;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Tlatoani on 1/3/17.
 */
public class ExprForObjects extends SimpleExpression<Object> {
    private Expression function;
    private Variable container;
    private Expression list;
    private boolean firstPattern;
    private VariableString listVar;
    private boolean listSettable;


    private void forEach(Event e, Runnable runnable) {
        if (listVar != null) {
            Iterator<Pair<String, Object>> iterator = ((Variable) list).variablesIterator(e);
            TreeMap<String, Object> listValue = new TreeMap<>();
            while (iterator.hasNext()) {
                Pair<String, Object> mapping = iterator.next();
                container.change(e, new Object[]{mapping.getValue()}, Changer.ChangeMode.SET);
                runnable.run();
                listValue.put(mapping.getFirst(), container.getSingle(e));
            }
            container.change(e, null, Changer.ChangeMode.DELETE);
            Variables.setVariable(listVar.toString(e), listValue, e, ((Variable) list).isLocal());
        } else if (listSettable) {
            boolean changed = false;
            Object[] listValue = list.getAll(e);
            Object[] newValue = new Object[listValue.length];
            for (int i = 0; i < listValue.length; i++) {
                container.change(e, new Object[]{listValue[i]}, Changer.ChangeMode.SET);
                runnable.run();
                newValue[i] = container.getSingle(e);
                changed = listValue[i] != newValue[i];
            }
            container.change(e, null, Changer.ChangeMode.DELETE);
            if (changed) {
                list.change(e, newValue, Changer.ChangeMode.SET);
            }
        } else {
            Object[] listValue = list.getAll(e);
            for (Object value : listValue) {
                container.change(e, new Object[]{value}, Changer.ChangeMode.SET);
                runnable.run();
            }
            container.change(e, null, Changer.ChangeMode.DELETE);
        }
    }

    @Override
    protected Object[] get(Event event) {
        ArrayList<Object> arrayList = new ArrayList<>();
        forEach(event, new Runnable() {
            @Override
            public void run() {
                arrayList.add(function.getSingle(event));
            }
        });
        return arrayList.toArray();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<?> getReturnType() {
        return function.getReturnType();
    }

    @Override
    public String toString(Event event, boolean b) {
        return function + (firstPattern ? " for " + container : "") + " in " + list;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        function = expressions[0];
        if (!(expressions[1] instanceof Variable)) {
            Skript.error("'" + expressions[1] + "' is not a variable!");
            return false;
        }
        container = (Variable) expressions[1];
        list = expressions[2];
        firstPattern = i == 0;
        if (container == null) {
            if (!(list instanceof Variable)) {
                Skript.error("'" + list + "' is not a list variable!");
                return false;
            }
            Variable listVar = (Variable) list;
            String fullName = listVar.toString();
            this.listVar = VariableString.newInstance(fullName.substring(1, fullName.length() - 1), StringMode.VARIABLE_NAME);
            String varName = fullName.substring(1, fullName.length() - 4);
            if (varName.charAt(varName.length() -1) == 's') { //If the variable name is plural make the container name singular
                varName = varName.substring(0, varName.length() - 1);
            }
            container = Variable.newInstance(varName, new Class[]{listVar.getReturnType()});
            listSettable = true;
        } else {
            List<Class> classes = Arrays.asList(Optional.ofNullable(list.acceptChange(Changer.ChangeMode.SET)).orElse(new Class[0]));
            listSettable = classes.contains(Object[].class) || classes.contains(Array.newInstance(list.getReturnType(), 0).getClass());
        }
        return true;
    }

    @Override
    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        Class[] classes = function.acceptChange(mode);
        if (classes != null) {
            Class[] result = new Class[classes.length];
            for (int i = 0; i < classes.length; i++) {
                result[i] = Array.newInstance(classes[i], 0).getClass();
            }
            return result;
        }
        return null;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        switch (mode) {
            case SET:
            case REMOVE:
            case REMOVE_ALL:
            case ADD:
            {
                Iterator iterator = Arrays.asList(delta).iterator();
                forEach(event, new Runnable() {
                    @Override
                    public void run() {
                        if (iterator.hasNext()) {
                            function.change(event, new Object[]{iterator.next()}, mode);
                        }
                    }
                });
            }
            case RESET:
            case DELETE:
            {
                forEach(event, new Runnable() {
                    @Override
                    public void run() {
                        function.change(event, null, mode);
                    }
                });
            }
        }
    }
}
