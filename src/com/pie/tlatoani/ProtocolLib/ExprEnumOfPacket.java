package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Tlatoani on 7/6/16.
 */
public class ExprEnumOfPacket extends SimpleExpression<String> {
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private Method getStructureModifier = null;
    private Class<? extends Enum> enumClass = null;

    /*//Adapted from ProtocolLib's EnumConverter class
    public static class EnumStringConverter implements EquivalentConverter<String> {
        public final Class<?> enumClass;

        public EnumStringConverter(Class<?> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        public Object getGeneric(String s) {
            return null;
        }

        @Override
        public String getSpecific(Object o) {
            return ((Enum) o).name();
        }

        @Override
        public Object getGeneric(Class<?> aClass, String s) {
            return Enum.valueOf((Class<Enum>) enumClass, s);
        }

        @Override
        public Class<String> getSpecificType() {
            return String.class;
        }
    }*/

    @Override
    protected String[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        StructureModifier<?> structureModifier = null;
        if (getStructureModifier == null) {
            structureModifier = packet.getModifier().withType(Enum.class);
        } else {
            try {
                structureModifier = (StructureModifier) getStructureModifier.invoke(packet);
            } catch (IllegalAccessException e) {
                Logging.debug(this, e);
                return new String[0];
            } catch (InvocationTargetException e) {
                Logging.debug(this, e);
                return new String[0];
            }
        }
        Object result = structureModifier.readSafely(index);
        return result == null ? new String[0] : new String[]{result.toString()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%string% penum %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        if (expressions[0] == null) {
            return true;
        }
        String enumMethodName;
        if (expressions[0] instanceof Literal<?>) {
            enumMethodName = ((Literal<String>) expressions[0]).getSingle();
        } else if (expressions[0] instanceof VariableString) {
            String fullstring = ((VariableString) expressions[0]).toString();
            enumMethodName = fullstring.substring(1, fullstring.length() - 1);
        } else {
            Skript.error("The string '" + expressions[0] + "' is not a literal string! Only literal strings can be used in the pjson expression!");
            return false;
        }
        Logging.debug(this, "Assumed method name: get" + enumMethodName);
        try {
            Method method = PacketContainer.class.getMethod("get" + enumMethodName + "s");
            Logging.debug(this, "Method Name: " + method.toString());
            getStructureModifier = method;
        } catch (NoSuchMethodException e) {
            Logging.debug(this, e);
            try {
                Method method = PacketContainer.class.getMethod("get" + enumMethodName);
                Logging.debug(this, "Method Name: " + method.toString());
                getStructureModifier = method;
            } catch (NoSuchMethodException e1) {
                Logging.debug(this, e1);
                Skript.error("The enum method name " + enumMethodName + " is not applicable for the '%string% penum %number% of %packet%' expression.");
                return false;
            }
        }
        PacketContainer testContainer = new PacketContainer(PacketType.Play.Server.CHAT);
        StructureModifier<?> structureModifier = null;
        try {
            structureModifier = (StructureModifier) getStructureModifier.invoke(testContainer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Logging.debug(this, e);
            Skript.error("The enum method name " + enumMethodName + " is not applicable for the '%string% penum %number% of %packet%' expression.");
            return false;
        }
        Class<?> enumClass = structureModifier.getFieldType();
        Logging.debug(this, "ENUM CLASS: " + enumClass);
        if (!Enum.class.isAssignableFrom(enumClass)) {
            Skript.error("The enum method name " + enumMethodName + " is not applicable for the '%string% penum %number% of %packet%' expression.");
            return false;
        }
        this.enumClass = (Class<? extends Enum>) enumClass;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        if (getStructureModifier == null) {
            try {
                StructureModifier structureModifier = packet.getModifier().withType(Enum.class);
                Class<?> enumClass = structureModifier.getField(index).getType();
                Object value = Enum.valueOf((Class<? extends Enum>) enumClass, ((String) delta[0]).toUpperCase());
                structureModifier.writeSafely(index, value);
            } catch (IllegalArgumentException e) {
                Logging.debug(this, e);
            }
        } else {
            try {
                StructureModifier structureModifier = (StructureModifier) getStructureModifier.invoke(packet);
                Object value = Enum.valueOf(enumClass, ((String) delta[0]).toUpperCase());
                structureModifier.writeSafely(index, value);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                Logging.debug(this, e);
            }
        }

    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
