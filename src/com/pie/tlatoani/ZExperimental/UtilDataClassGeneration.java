package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.lang.Condition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Tlatoani on 8/17/17.
 */
public class UtilDataClassGeneration {
    public static final String TAB = "    ";

    //TODO add equals method
    public static String[] generateClassCode(String typeName, Property[] properties) {
        String className = "MundoSKDataClass" + typeName.toUpperCase();
        ArrayList<String> lines = new ArrayList<>();
        lines.add("public class " + className + " {");
        for (Property property : properties) {
            lines.add(TAB + "public final " + property.type.getName() + " " + property.javaName + ";");
        }
        lines.add("");
        lines.add(TAB + "private " + className + "(" +
                Arrays
                        .stream(properties)
                        .map(property -> property.type.getName() + " " + property.javaName)
                        .collect(Collectors.joining(", ")) + ") {");
        for (Property property : properties) {
            lines.add(TAB + TAB + "this." + property.javaName + " = " + property.javaName + ";");
        }
        lines.add(TAB + "}");
        lines.add("");
        lines.add(TAB + "public " + className + " create(" +
                Arrays
                        .stream(properties)
                        .map(property -> property.type.getName() + " " + property.javaName)
                        .collect(Collectors.joining(", ")) + ") {");
        for (Property property : properties) {
            if (!property.nullable) {
                lines.add(TAB + TAB + "if (" + property.javaName + " == null) return null;");
            }
        }
        lines.add(TAB + TAB + "return new " + className + "(" + Arrays
                .stream(properties)
                .map(property -> property.javaName)
                .collect(Collectors.joining(", ")) + ");");
        lines.add(TAB + "}");
        for (Property property : properties) {
            lines.add("");
            lines.add(TAB + "public " + className + " set" + property.javaName.toUpperCase() + "(" + property.type.getName() + " " + property.javaName + ") {");
            if (!property.nullable) {
                lines.add(TAB + TAB + "if (" + property.javaName + " == null) return null;");
            }
            lines.add(TAB + TAB + "return new " + className + "(" + Arrays
                    .stream(properties)
                    .map(property1 -> property1.javaName)
                    .collect(Collectors.joining(", ")) + ");");
            lines.add(TAB + "}");
        }
        lines.add("}");

        return lines.toArray(new String[0]);
    }

    public static class Property {
        public final Class type;
        public final String name;
        public final String javaName;
        public final boolean nullable;

        public Property(Class type, String name, boolean nullable) {
            this.type = type;
            this.name = name;
            this.javaName = name.replace(' ', '_');
            this.nullable = nullable;
        }
    }
}
