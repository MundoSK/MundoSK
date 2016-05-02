package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Tlatoani on 5/2/16.
 */
public class ExprObjectOfPacket extends SimpleExpression<Object> {
    private Method getObjects = null;
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private static Field structureModifier;
    private Boolean isSingle = true;
    private Class aClass;

    static {
        try {
            structureModifier = PacketContainer.class.getDeclaredField("structureModifier");
            structureModifier.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object[] get(Event event) {
        StructureModifier structureModifier = null;
        if (getObjects != null) {
            try {
                structureModifier = (StructureModifier) getObjects.invoke(packetContainerExpression.getSingle(event));
            } catch (IllegalAccessException e) {
                Mundo.debug(this, e);
            } catch (InvocationTargetException e) {
                Mundo.debug(this, e);
            }
        } else {
            try {
                structureModifier = (StructureModifier) ExprObjectOfPacket.structureModifier.get(packetContainerExpression.getSingle(event));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new Object[] {structureModifier.readSafely(index.getSingle(event).intValue())};
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%type% %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        Literal<ClassInfo<?>> literal = (Literal<ClassInfo<?>>) expressions[0];
        aClass = literal.getSingle().getC();
        String classname = aClass.getSimpleName();
        if (aClass == Object.class) {
            return true;
        }
        Mundo.debug(this, "Class simple name: " + classname);
        String pluralclassname;
        if (i == 1) {
            pluralclassname = classname + "Arrays";
            isSingle = false;
        } else if (classname.substring(classname.length() - 1).equals("y")) {
            pluralclassname = classname.substring(0, classname.length() - 1) + "ies";
        } else {
            pluralclassname = classname + "s";
        }
        Mundo.debug(this, "Class plural name: " + pluralclassname);
        try {
            Method method = PacketContainer.class.getMethod("get" + pluralclassname);
            Mundo.debug(this, "Method: " + method.toString());
            getObjects = method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        }
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        StructureModifier structureModifier = null;
        if (getObjects != null) {
            try {
                structureModifier = (StructureModifier) getObjects.invoke(packetContainerExpression.getSingle(event));
            } catch (IllegalAccessException e) {
                Mundo.debug(this, e);
            } catch (InvocationTargetException e) {
                Mundo.debug(this, e);
            }
        } else {
            try {
                structureModifier = (StructureModifier) ExprObjectOfPacket.structureModifier.get(packetContainerExpression.getSingle(event));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        structureModifier.writeSafely(index.getSingle(event).intValue(), delta[0]);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            if (isSingle) {
                return CollectionUtils.array(aClass);
            } else {
                return CollectionUtils.array(Object[].class);
            }
        }
        return null;
    }
}
