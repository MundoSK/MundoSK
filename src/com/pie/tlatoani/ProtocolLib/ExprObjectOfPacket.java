package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtType;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Util.UtilReflection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
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

    /*

    public static JSONObject fromNBTBase(NbtBase nbtBase) {
        JSONObject result = new JSONObject();
        if (nbtBase.getType() != NbtType.TAG_COMPOUND) {
            result.put("type", nbtBase.getType().toString().substring(4).toLowerCase());
        }
        if (nbtBase != null)  {
            switch (nbtBase.getType()) {
                case TAG_BYTE:
                case TAG_SHORT:
                case TAG_INT:
                case TAG_LONG:
                case TAG_FLOAT:
                case TAG_DOUBLE:
                case TAG_STRING:
                    result.put("value", nbtBase.getValue());
                    return result;
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
                        if (member.getType() == NbtType.TAG_END) continue;
                        result.put(member.getName(), fromNBTBase(member));
                    }
            }
        }
        return result;
    }

    public static NbtBase toNBTBase(JSONObject value, String name) {
        Object maybeType = value.get("type");
        if (maybeType instanceof String) {
            try {
                NbtType type = NbtType.valueOf("TAG_" + ((String) maybeType).toUpperCase());
                Object val = value.get("value");
                Number number = val instanceof Number ? (Number) val : 0;
                switch (type) {
                    case TAG_BYTE:
                        return NbtFactory.of(name, number.byteValue());
                    case TAG_SHORT:
                        return NbtFactory.of(name, number.shortValue());
                    case TAG_INT:
                        return NbtFactory.of(name, number.intValue());
                    case TAG_LONG:
                        return NbtFactory.of(name, number.longValue());
                    case TAG_FLOAT:
                        return NbtFactory.of(name, number.floatValue());
                    case TAG_DOUBLE:
                        return NbtFactory.of(name, number.doubleValue());
                    case TAG_STRING:
                        return NbtFactory.of(name, val instanceof String ? (String) val : null);
                    case TAG_BYTE_ARRAY:
                        ArrayList<Number> byteList = new ArrayList<>();
                        value.forEach(new BiConsumer() {
                            @Override
                            public void accept(Object o, Object o2) {
                                try {
                                    byteList.set(Integer.parseInt((String) o) - 1, (Number) o2);
                                } catch (NumberFormatException | ClassCastException e) {}
                            }
                        });
                        byte[] bytes = new byte[byteList.size()];
                        for (int i = 0; i < bytes.length; i++) {
                            bytes[i] = byteList.get(i).byteValue();
                        }
                        return NbtFactory.of(name, bytes);
                    case TAG_INT_ARRAY:
                        ArrayList<Number> intList = new ArrayList<>();
                        value.forEach(new BiConsumer() {
                            @Override
                            public void accept(Object o, Object o2) {
                                try {
                                    intList.set(Integer.parseInt((String) o) - 1, (Number) o2);
                                } catch (NumberFormatException | ClassCastException e) {}
                            }
                        });
                        byte[] ints = new byte[intList.size()];
                        for (int i = 0; i < ints.length; i++) {
                            ints[i] = intList.get(i).byteValue();
                        }
                        return NbtFactory.of(name, ints);
                    case TAG_LIST:
                        ArrayList<NbtBase> nbtBases = new ArrayList<>();
                        value.forEach(new BiConsumer() {
                            @Override
                            public void accept(Object o, Object o2) {
                                try {
                                    nbtBases.set(Integer.parseInt((String) o) - 1, toNBTBase((JSONObject) o2, ""));
                                } catch (NumberFormatException | ClassCastException e) {}
                            }
                        });
                        return NbtFactory.ofList(name, nbtBases);
                    case TAG_END:
                        throw new IllegalArgumentException("TAG_END base");
                }
            } catch (IllegalArgumentException e) {}
            ArrayList<NbtBase> nbtBases = new ArrayList<>();
            value.forEach(new BiConsumer() {
                @Override
                public void accept(Object o, Object o2) {
                    try {
                        nbtBases.add(toNBTBase((JSONObject) o2, (String) o));
                    } catch (ClassCastException e) {}
                }
            });
            return NbtFactory.ofCompound(name, nbtBases);
        }
    }


    public static void setNBTBase(Object value, NbtBase nbtBase) {
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
                        setNBTBase(((JSONObject) value).get("" + i), member);
                    }
                    break;
                case TAG_COMPOUND:
                    for (NbtBase member : (NbtCompound) nbtBase) {
                        setNBTBase(((JSONObject) value).get(member.getName()), member);
                    }
            }
        }
    }
    */
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

            @Override
            public Class<Location> getType() {
                return Location.class;
            }
        });

        registerSingleConverter("material", new PacketInfoConverter<ItemStack>() {
            @Override
            public ItemStack get(PacketContainer packet, Integer index) {
                Material material = packet.getBlocks().readSafely(index);
                return material == null ? null : new ItemStack(material);
            }

            @Override
            public void set(PacketContainer packet, Integer index, ItemStack value) {
                Material material = value.getType();
                packet.getBlocks().writeSafely(index, material);
            }

            @Override
            public Class<ItemStack> getType() {
                return ItemStack.class;
            }
        });

        //Thanks to ashcr0w for help with the following converter

        try {
            Class nmsItemClass = UtilReflection.getMinecraftClass("Item");
            UtilReflection.MethodInvoker asNMSCopy = UtilReflection.getTypedMethod(
                    UtilReflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    "asNMSCopy",
                    UtilReflection.getMinecraftClass("ItemStack"),
                    ItemStack.class
            );
            UtilReflection.MethodInvoker getNMSItem = UtilReflection.getTypedMethod(
                    UtilReflection.getMinecraftClass("ItemStack"),
                    "getItem",
                    nmsItemClass
            );
            UtilReflection.MethodInvoker asNewCraftStack = UtilReflection.getTypedMethod(
                    UtilReflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    "asNewCraftStack",
                    UtilReflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    nmsItemClass
            );
            EquivalentConverter<ItemStack> itemConvert = new EquivalentConverter<ItemStack>() {
                @Override
                public ItemStack getSpecific(Object o) {
                    return (ItemStack) asNewCraftStack.invoke(null, o);
                }

                @Override
                public Object getGeneric(Class<?> aClass, ItemStack itemStack) {
                    return getNMSItem.invoke(asNMSCopy.invoke(null, itemStack));
                }

                @Override
                public Class<ItemStack> getSpecificType() {
                    return ItemStack.class;
                }
            };
            registerSingleConverter("item", new PacketInfoConverter<ItemStack>() {
                @Override
                public ItemStack get(PacketContainer packet, Integer index) {
                    StructureModifier<ItemStack> structureModifier = packet.getModifier().withType(nmsItemClass, itemConvert);
                    return structureModifier.readSafely(index);
                }

                @Override
                public void set(PacketContainer packet, Integer index, ItemStack value) {
                    /*ItemStack itemStack;
                    if (value instanceof ItemStack) {
                        itemStack = (ItemStack) value;
                    } else if (value instanceof ItemType) {
                        itemStack = ((ItemType) value).getRandom();
                    } else {
                        itemStack = null;
                    }*/
                    StructureModifier<ItemStack> structureModifier = packet.getModifier().withType(nmsItemClass, itemConvert);
                    structureModifier.writeSafely(index, value);
                }

                @Override
                public Class<ItemStack> getType() {
                    return ItemStack.class;
                }
            });
        } catch (Exception e) {
            Mundo.reportException(ExprObjectOfPacket.class, e);
        }

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
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.CHAT);
        try {
            StructureModifier tempStructureModifier = (StructureModifier) method.invoke(packetContainer);
            if (!tempStructureModifier.getFieldType().isArray()) {
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
            } else {
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
                            Object[] valueArray = (Object[]) value;
                            Object[] result = (Object[]) Array.newInstance(structureModifier.getFieldType().getComponentType(), valueArray.length);
                            for (int i = 0; i < valueArray.length; i++) {
                                result[i] = valueArray[i];
                            }
                            structureModifier.writeSafely(index, result);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            Mundo.debug(this, e);
                        }
                    }
                };
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Mundo.reportException(ExprObjectOfPacket.class, e);
            return null;
        }
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
            aClass = Optional.ofNullable(converter.getType()).orElse(aClass);
            Mundo.debug(this, "Converter to PLib type: " + key + ", aClass = " + aClass);
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
