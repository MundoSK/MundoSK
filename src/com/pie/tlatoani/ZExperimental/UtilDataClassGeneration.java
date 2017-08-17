package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.lang.Condition;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/17/17.
 */
public class UtilDataClassGeneration {

    public String[] generateClassCode(String typeName) {
        return new String[]{
                "public class DataType" + typeName + " {",
                "   "
        };
    }

    public static class Property {
        public final Class type;
        public final String name;
        public final boolean nullable;
        public final Optional<Condition> condition;

        public Property(Class type, String name, boolean nullable, Optional<Condition> condition) {
            this.type = type;
            this.name = name;
            this.nullable = nullable;
            this.condition = condition;
        }
    }
}
