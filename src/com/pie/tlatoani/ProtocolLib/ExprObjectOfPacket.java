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
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.pie.tlatoani.Mundo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 5/2/16.
 */
public class ExprObjectOfPacket extends SimpleExpression<Object> {
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private boolean isSingle = true;
    private Class aClass = Object.class;
    private PacketInfoConverter converter = null;

    private static Map<String, PacketInfoConverter> singleConverters = new LinkedHashMap<>();
    private static Map<String, PacketInfoConverter<Object[]>> pluralConverters = new LinkedHashMap<>();

    //Util

    public static Object fromNBTBase(NbtBase nbtBase) {
        JSONObject result = null;
        if (nbtBase != null)  {
            switch (nbtBase.getType()) {
                case TAG_BYTE:
                case TAG_SHORT:
                case TAG_INT:
                case TAG_LONG:
                case TAG_FLOAT:
                case TAG_DOUBLE:
                case TAG_STRING:
                    return nbtBase.getValue();
                case TAG_BYTE_ARRAY:
                    result = new JSONObject();
                    for (int i = 0; i < ((byte[]) nbtBase.getValue()).length; i++) {
                        result.put("" + (i + 1), ((byte[]) nbtBase.getValue())[i]);
                    }
                    break;
                case TAG_INT_ARRAY:
                    result = new JSONObject();
                    for (int i = 0; i < ((int[]) nbtBase.getValue()).length; i++) {
                        result.put("" + (i + 1), ((int[]) nbtBase.getValue())[i]);
                    }
                    break;
                case TAG_LIST:
                    result = new JSONObject();
                    int i = 0;
                    for (Object o : (List) nbtBase.getValue()) {
                        i++;
                        result.put("" + i, o);
                    }
                    break;
                case TAG_COMPOUND:
                    result = new JSONObject();
                    for (NbtBase member : (NbtCompound) nbtBase) {
                        result.put(member.getName(), fromNBTBase(member));
                    }
            }
        }
        return result;
    }

    public static void toNBTBase(Object value, NbtBase nbtBase) {
        if (nbtBase != null && value != null) {
            switch (nbtBase.getType()) {
                case TAG_BYTE:
                    nbtBase.setValue(((Number) value).byteValue());
                    break;
                case TAG_SHORT:
                    nbtBase.setValue(((Number) value).shortValue());
                    break;
                case TAG_INT:
                    nbtBase.setValue(((Number) value).intValue());
                    break;
                case TAG_LONG:
                    nbtBase.setValue(((Number) value).longValue());
                    break;
                case TAG_FLOAT:
                    nbtBase.setValue(((Number) value).floatValue());
                    break;
                case TAG_DOUBLE:
                    nbtBase.setValue(((Number) value).byteValue());
                    break;
                case TAG_STRING:
                    nbtBase.setValue(value);
                    break;
                case TAG_BYTE_ARRAY:
                    byte[] bytes = new byte[((JSONObject) value).size()];
                    ((JSONObject) value).forEach(new BiConsumer() {
                        @Override
                        public void accept(Object o, Object o2) {
                            try {
                                bytes[Integer.parseInt((String) o) - 1] = (byte) o2;
                            } catch (NumberFormatException e) {
                                //Ignore non-integer indexes
                            }
                        }
                    });
                    nbtBase.setValue(bytes);
                    break;
                case TAG_INT_ARRAY:
                    int[] ints = new int[((JSONObject) value).size()];
                    ((JSONObject) value).forEach(new BiConsumer() {
                        @Override
                        public void accept(Object o, Object o2) {
                            try {
                                ints[Integer.parseInt((String) o) - 1] = (int) o2;
                            } catch (NumberFormatException e) {
                                //Ignore non-integer indexes
                            }
                        }
                    });
                    nbtBase.setValue(ints);
                    break;
                case TAG_LIST:
                    int i = 0;
                    for (NbtBase member : (List<NbtBase>) nbtBase.getValue()) {
                        i++;
                        toNBTBase(((JSONObject) value).get("" + i), member);
                    }
                    break;
                case TAG_COMPOUND:
                    for (NbtBase member : (NbtCompound) nbtBase) {
                        toNBTBase(((JSONObject) value).get(member.getName()), member);
                    }
            }
        }
    }

    static {

        //Single Converters

        registerSingleConverter("object", new PacketInfoConverter<Object>() {
            @Override
            public Object get(PacketContainer packet, Integer index) {
                return packet.getModifier().readSafely(index);
            }

            @Override
            public void set(PacketContainer packet, Integer index, Object value) {
                packet.getModifier().writeSafely(index, value);
            }
        });

        registerSingleConverter("location", new PacketInfoConverter<Location>() {
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

        registerSingleConverter("nbt", new PacketInfoConverter<Object>() {
            @Override
            public Object get(PacketContainer packet, Integer index) {
                return fromNBTBase(packet.getNbtModifier().readSafely(index));
            }

            @Override
            public void set(PacketContainer packet, Integer index, Object value) {
                NbtBase nbtBase = packet.getNbtModifier().readSafely(index);
                toNBTBase(value, nbtBase);
                packet.getNbtModifier().writeSafely(index, nbtBase);
            }
        });

        //Plural Converters

        registerPluralConverter("collection", new PacketInfoConverter<Object[]>() {
            @Override
            public Object[] get(PacketContainer packet, Integer index) {
                Collection collection = packet.getSpecificModifier(Collection.class).readSafely(index);
                return collection == null ? new Object[0] : collection.toArray();
            }

            @Override
            public void set(PacketContainer packet, Integer index, Object[] value) {
                packet.getSpecificModifier(Collection.class).writeSafely(index, Arrays.asList(value));
            }
        });
    }

    private static <T> void registerSingleConverter(String key, PacketInfoConverter<T> converter) {
        singleConverters.put(key, converter);
    }

    private static void registerPluralConverter(String key, PacketInfoConverter<Object[]> converter) {
        pluralConverters.put(key, converter);
    }

    private static PacketInfoConverter getConverter(String key, Boolean isSingle) {
        return isSingle ? singleConverters.get(key) : pluralConverters.get(key);
    }

    public static String getConverterNamesPattern(Boolean isSingle) {
        String result = "";
        int i = 0;
        for (String name : isSingle ? singleConverters.keySet() : pluralConverters.keySet()) {
            i++;
            result += "|" + i + "¦" + name;
        }
        return result;
    }

    public static String getConverterNameByIndex(int index, Boolean isSingle) {
        int i = 0;
        for (String name : isSingle ? singleConverters.keySet() : pluralConverters.keySet()) {
            i++;
            if (i == index) return name;
        }
        return null;
    }

    private static PacketInfoConverter createConverter(Method method) {
        return new PacketInfoConverter() {
            @Override
            public Object get(PacketContainer packet, Integer index) {
                try {
                    StructureModifier structureModifier = (StructureModifier) method.invoke(packet);
                    return structureModifier.readSafely(index);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Mundo.debug(this, e);
                    return null;
                }
            }

            @Override
            public void set(PacketContainer packet, Integer index, Object value) {
                try {
                    StructureModifier structureModifier = (StructureModifier) method.invoke(packet);
                    structureModifier.writeSafely(index, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Mundo.debug(this, e);
                }
            }
        };
    }

    @Override
    protected Object[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        if (isSingle) {
            return new Object[]{converter.get(packet, index)};
        } else {
            return (Object[]) converter.get(packet, index);
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
        return "%type/string% packetinfo %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        isSingle = i % 2 == 0;
        String key;
        String methodGetName;
        if (expressions[0] == null) {
            key = getConverterNameByIndex(parseResult.mark, isSingle);
            methodGetName = null;
        } else if (expressions[0] instanceof Literal && expressions[0].getReturnType() == ClassInfo.class) {
            ClassInfo classInfo = ((Literal<ClassInfo<?>>) expressions[0]).getSingle();
            key = classInfo.getCodeName();
            aClass = classInfo.getC();
            String classname = aClass.getSimpleName();
            Mundo.debug(this, "Class simple name: " + classname);
            if (!isSingle) {
                methodGetName = classname + "Arrays";
                isSingle = false;
            } else if (classname.substring(classname.length() - 1).equals("y")) {
                methodGetName = classname.substring(0, classname.length() - 1) + "ies";
            } else {
                methodGetName = classname + "s";
            }
        } else {
            if (expressions[0] instanceof VariableString) {
                String fullstring = ((VariableString) expressions[0]).toString();
                methodGetName = fullstring.substring(1, fullstring.length() - 1);
            } else {
                Skript.error("The string '" + expressions[0] + "' is not a literal string! Only literal strings can be used in the %string% pinfo expression!");
                return false;
            }
            key = methodGetName.toLowerCase();
        }
        converter = getConverter(key, isSingle);
        if (converter != null) {
            Mundo.debug(this, "Converter to PLib type: " + key);
            return true;
        }
        try {
            Method method = PacketContainer.class.getMethod("get" + methodGetName);
            Mundo.debug(this, "Method: " + method.toString());
            converter = createConverter(method);
            return true;
        } catch (NoSuchMethodException e) {
            Mundo.debug(this, e);
        }
        Skript.error(key + " is not applicable for the '%type/string% pinfo [array] %number% of %packet%' expression.");
        return false;

    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        converter.set(packet, index, isSingle ? delta[0] : delta);
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
