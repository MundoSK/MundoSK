package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.google.common.collect.ImmutableSet;
import com.pie.tlatoani.Mundo;

import java.util.HashMap;

/**
 * Created by Tlatoani on 4/4/17.
 */
public class ExpressionConstraints {
    public final ImmutableSet<Type> types;
    public final Kleenean isLiteral;
    public final int time;
    public final boolean nullable;

    public static class Type {
        public final ClassInfo classInfo;
        public final boolean isSingle;

        public Type(ClassInfo classInfo, boolean isSingle) {
            this.classInfo = classInfo;
            this.isSingle = isSingle;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof Type) {
                Type type = (Type) object;
                return isSingle == type.isSingle && classInfo.equals(type.classInfo);
            }
            return false;
        }
    }

    private ExpressionConstraints(ImmutableSet<Type> types, Kleenean isLiteral, int time, boolean nullable) {
        this.types = types;
        this.isLiteral = isLiteral;
        this.time = time;
        this.nullable = nullable;
    }

    public static ExpressionConstraints fromString(String string) {
        Kleenean isLiteral;
        if (string.charAt(0) == '*') {
            isLiteral = Kleenean.TRUE;
            string = string.substring(1);
        } else if (string.charAt(0) == '~') {
            isLiteral = Kleenean.FALSE;
            string = string.substring(1);
        } else {
            isLiteral = Kleenean.UNKNOWN;
        }
        boolean nullable;
        if (string.charAt(0) == '-') {
            nullable = true;
            string = string.substring(0);
        } else {
            nullable = false;
        }
        int time;
        if (string.endsWith("@1")) {
            time = 1;
            string = string.substring(0, string.length() - 2);
        } else if (string.endsWith("@-1")) {
            time = -1;
            string = string.substring(0, string.length() - 2);
        } else {
            time = 0;
        }
        ImmutableSet.Builder<Type> builder = ImmutableSet.builder();
        for (String typeStr : string.split("/")) {
            boolean isSingle;
            if (typeStr.endsWith("s")) {
                isSingle = false;
                typeStr = typeStr.substring(0, typeStr.length() - 1);
            } else {
                isSingle = true;
            }
            ClassInfo classInfo = Classes.getClassInfo(typeStr);
            builder.add(new Type(classInfo, isSingle));
        }
        return new ExpressionConstraints(builder.build(), isLiteral, time, nullable);
    }

    public Class getSuperClass() {
        Class[] classes = new Class[types.size()];
        int i = 0;
        for (Type type : types) {
            classes[i] = type.classInfo.getC();
            i++;
        }
        return Mundo.commonSuperClass(classes);
    }

    public static class Collective {
        private HashMap<String, ExpressionConstraints> constraintsMap = new HashMap<>();

        public void addConstraints(String variable, ExpressionConstraints constraints) {
            ExpressionConstraints prevConstraints = constraintsMap.get(variable);
            if (prevConstraints != null) {
                ImmutableSet.Builder builder = ImmutableSet.builder();
                builder.addAll(prevConstraints.types);
                builder.addAll(constraints.types);
                Kleenean isLiteral = constraints.isLiteral == prevConstraints.isLiteral ? constraints.isLiteral : Kleenean.UNKNOWN;
                int time = constraints.time == prevConstraints.time ? constraints.time : 0;
                boolean nullable = constraints.nullable || prevConstraints.nullable;
                ExpressionConstraints newConstraints = new ExpressionConstraints(builder.build(), isLiteral, time, nullable);
                constraintsMap.put(variable, newConstraints);
            } else {
                constraintsMap.put(variable, constraints);
            }
        }
    }
}
