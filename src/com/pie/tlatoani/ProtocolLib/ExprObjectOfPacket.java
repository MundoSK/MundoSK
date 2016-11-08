package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

import java.awt.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Tlatoani on 5/2/16.
 */
public class ExprObjectOfPacket extends SimpleExpression<Object> {
    private Method getObjects = null;
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private static Field structureModifier;
    private boolean isSingle = true;
    private boolean collection = false;
    private Class aClass;
    private PacketInfoConverter converter;

    public static Map<Class, PacketInfoConverter> converterMap = new HashMap<>();

    static {
        try {
            structureModifier = PacketContainer.class.getDeclaredField("structureModifier");
            structureModifier.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        //Converters

        registerConverter(Location.class, new PacketInfoConverter<Location>() {
            @Override
            public Location get(PacketContainer packet, Integer index) {
                StructureModifier<BlockPosition> structureModifier = packet.getBlockPositionModifier();
                BlockPosition blockPosition = structureModifier.readSafely(index);
                if (blockPosition == null) return null;
                return blockPosition.toLocation(Bukkit.getWorlds().get(0));
            }

            @Override
            public void set(PacketContainer packet, Integer index, Location value) {
                StructureModifier<BlockPosition> structureModifier = packet.getBlockPositionModifier();
                BlockPosition blockPosition = new BlockPosition(value.toVector());
                structureModifier.writeSafely(index, blockPosition);
            }
        });
    }

    private static <T> void registerConverter(Class<T> aClass, PacketInfoConverter<T> converter) {
        converterMap.put(aClass, converter);
    }

    private static <T> PacketInfoConverter<T> getConverter(Class<T> aClass, Boolean isSingle) {
        return converterMap.get(aClass);
    }

    @Override
    protected Object[] get(Event event) {
        StructureModifier structureModifier = null;
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        if (getObjects != null) {
            try {
                structureModifier = (StructureModifier) getObjects.invoke(packet);
            } catch (IllegalAccessException e) {
                Mundo.debug(this, e);
            } catch (InvocationTargetException e) {
                Mundo.debug(this, e);
            }
        } else if (converter != null && isSingle) {
            return new Object[] {converter.get(packet, index)};
        } else if (collection) {
            Collection collection1 = packet.getSpecificModifier(Collection.class).readSafely(index);
            return collection1 == null ? new Object[]{} : collection1.toArray();
        } else {
            try {
                structureModifier = (StructureModifier) ExprObjectOfPacket.structureModifier.get(packetContainerExpression.getSingle(event));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (isSingle) {
            return new Object[] {structureModifier.readSafely(index)};
        } else {
            return (Object[]) structureModifier.readSafely(index);
        }
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<?> getReturnType() {
        return aClass;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%type% packetinfo %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (i == 0 || i == 1) {
            Literal<ClassInfo<?>> literal = (Literal<ClassInfo<?>>) expressions[0];
            index = (Expression<Number>) expressions[1];
            packetContainerExpression = (Expression<PacketContainer>) expressions[2];
            aClass = literal.getSingle().getC();
            converter = getConverter(aClass, true);
            if (converter != null) {
                Mundo.debug(this, "Converter to PLib type: " + aClass);
                isSingle = true;
                return true;
            }
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
                Mundo.debug(this, e);
                Skript.error("The type " + literal + " is not applicable for the '%type% %number% of %packet%' expression.");
                return false;
            }
            return true;
        } else {
            String classname;
            if (expressions[0] instanceof VariableString) {
                String fullstring = ((VariableString) expressions[0]).toString();
                classname = fullstring.substring(1, fullstring.length() - 1);
            } else {
                Skript.error("The string '" + expressions[0] + "' is not a literal string! Only literal strings can be used in the %string% pinfo expression!");
                return false;
            }
            index = (Expression<Number>) expressions[1];
            packetContainerExpression = (Expression<PacketContainer>) expressions[2];
            if (classname.equalsIgnoreCase("collection")) {
                isSingle = false;
                collection = true;
                aClass = Object.class;
                return true;
            }
            Mundo.debug(this, "Method name without 'get': " + classname);
            try {
                Method method = PacketContainer.class.getMethod("get" + classname);
                Mundo.debug(this, "Method: " + method.toString());
                getObjects = method;
                aClass = Object.class;
            } catch (NoSuchMethodException e) {
                Mundo.debug(this, e);
                Skript.error("There is no packet info method called 'get" + classname + "'!");
                return false;
            }
            return true;
        }
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        StructureModifier structureModifier = null;
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        if (getObjects != null) {
            try {
                structureModifier = (StructureModifier) getObjects.invoke(packet);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Mundo.debug(this, e);
            }
        } else if (converter != null && isSingle) {
            converter.set(packet, index, delta[0]);
        } else if (collection) {
            packet.getSpecificModifier(Collection.class).writeSafely(index, Arrays.asList(delta));
        } else {
            try {
                structureModifier = (StructureModifier) ExprObjectOfPacket.structureModifier.get(packet);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (structureModifier != null) {
            if (isSingle) {
                structureModifier.writeSafely(index, delta[0]);
            } else {
                Object[] result = (Object[]) Array.newInstance(aClass, delta.length);
                for (int i = 0; i < delta.length; i++) {
                    result[i] = delta[i];
                }
                structureModifier.writeSafely(index, result);
            }
        }
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
