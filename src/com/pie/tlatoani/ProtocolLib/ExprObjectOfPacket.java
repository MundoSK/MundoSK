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
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Reflection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Tlatoani on 5/2/16.
 */
public class ExprObjectOfPacket extends SimpleExpression<Object> {
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private boolean isSingle;
    private Class aClass;
    private PacketInfoConverter converter;

    private static Map<String, PacketInfoConverter> singleConverters = new LinkedHashMap<>();
    private static Map<String, PacketInfoConverter<Object[]>> pluralConverters = new LinkedHashMap<>();

    static void registerConverters() {

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

        registerSingleConverter("location", new PacketInfoConverter<Location>(Location.class) {
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

        registerSingleConverter("uuid", new PacketInfoConverter<String>(String.class) {
            @Override
            public String get(PacketContainer packet, Integer index) {
                return Optional.ofNullable(packet.getUUIDs().readSafely(index)).map(UUID::toString).orElse(null);
            }

            @Override
            public void set(PacketContainer packet, Integer index, String value) {
                packet.getUUIDs().writeSafely(index, UUID.fromString(value));
            }
        });

        registerSingleConverter("material", new PacketInfoConverter<ItemStack>(ItemStack.class) {
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
        });

        //Thanks to ashcr0w for help with the following converter

        try {
            Class nmsItemClass = Reflection.getMinecraftClass("Item");
            Reflection.MethodInvoker asNMSCopy = Reflection.getTypedMethod(
                    Reflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    "asNMSCopy",
                    Reflection.getMinecraftClass("ItemStack"),
                    ItemStack.class
            );
            Reflection.MethodInvoker getNMSItem = Reflection.getTypedMethod(
                    Reflection.getMinecraftClass("ItemStack"),
                    "getItem",
                    nmsItemClass
            );
            Reflection.MethodInvoker asNewCraftStack = Reflection.getTypedMethod(
                    Reflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    "asNewCraftStack",
                    Reflection.getCraftBukkitClass("inventory.CraftItemStack"),
                    nmsItemClass
            );
            EquivalentConverter<ItemStack> itemConvert = new EquivalentConverter<ItemStack>() {
                @Override
                public Object getGeneric(ItemStack itemStack) {
                    return null;
                }

                @Override
                public ItemStack getSpecific(Object o) {
                    return (ItemStack) asNewCraftStack.invoke(null, o);
                }

                public Object getGeneric(Class<?> aClass, ItemStack itemStack) {
                    return getNMSItem.invoke(asNMSCopy.invoke(null, itemStack));
                }

                @Override
                public Class<ItemStack> getSpecificType() {
                    return ItemStack.class;
                }
            };
            registerSingleConverter("item", new PacketInfoConverter<ItemStack>(ItemStack.class) {
                @Override
                public ItemStack get(PacketContainer packet, Integer index) {
                    StructureModifier<ItemStack> structureModifier = packet.getModifier().withType(nmsItemClass, itemConvert);
                    return structureModifier.readSafely(index);
                }

                @Override
                public void set(PacketContainer packet, Integer index, ItemStack value) {
                    StructureModifier<ItemStack> structureModifier = packet.getModifier().withType(nmsItemClass, itemConvert);
                    structureModifier.writeSafely(index, value);
                }
            });
        } catch (Exception e) {
            Logging.reportException(ExprObjectOfPacket.class, e);
        }

        registerSingleConverter("blockdata", new PacketInfoConverter<ItemStack>(ItemStack.class) {
            @Override
            public ItemStack get(PacketContainer packet, Integer index) {
                WrappedBlockData blockData = packet.getBlockData().readSafely(index);
                ItemStack itemStack = new ItemStack(blockData.getType());
                itemStack.setData(new MaterialData(blockData.getType(), new Integer(blockData.getData()).byteValue()));
                return itemStack;
            }

            @Override
            public void set(PacketContainer packet, Integer index, ItemStack value) {
                WrappedBlockData blockData = WrappedBlockData.createData(value.getType(), value.getData().getData());
                packet.getBlockData().writeSafely(index, blockData);
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

        Reflection.ConstructorInvoker packetDataSerializerConstructor = Reflection.getConstructor(
                Reflection.getMinecraftClass("PacketDataSerializer"), ByteBuf.class);

        registerPluralConverter("bytebuffer", new PacketInfoConverter<Object[]>(Number[].class) {
            @Override
            public Object[] get(PacketContainer packet, Integer index) {
                ByteBuf byteBuf = packet.getSpecificModifier(ByteBuf.class).readSafely(index);
                byte[] bytes = byteBuf.array();
                Object[] result = new Object[bytes.length];
                for (int i = 0; i < bytes.length; i++) {
                    result[i] = bytes[i];
                }
                return result;
            }

            @Override
            public void set(PacketContainer packet, Integer index, Object[] value) {
                byte[] bytes = new byte[value.length];
                for (int i = 0; i < value.length; i++) {
                    bytes[i] = ((Number) value[i]).byteValue();
                }
                ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
                ByteBuf packetDataSerializer = (ByteBuf) packetDataSerializerConstructor.invoke(byteBuf);
                packet.getSpecificModifier(ByteBuf.class).writeSafely(index, packetDataSerializer);
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
                            Logging.debug(this, e);
                            return null;
                        }
                    }

                    @Override
                    public void set(PacketContainer packet, Integer index, Object value) {
                        try {
                            StructureModifier structureModifier = (StructureModifier) method.invoke(packet);
                            structureModifier.writeSafely(index, value);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            Logging.debug(this, e);
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
                            Logging.debug(this, e);
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
                            Logging.debug(this, e);
                        }
                    }
                };
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            Logging.debug(ExprObjectOfPacket.class, e);
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
        aClass = isSingle ? Object.class : Object[].class;
        String key;
        String methodGetName;
        if (expressions[0] == null) {
            key = getConverterNameByIndex(parseResult.mark, isSingle);
            methodGetName = null;
        } else if (expressions[0] instanceof Literal && expressions[0].getReturnType() == ClassInfo.class) {
            ClassInfo classInfo = ((Literal<ClassInfo<?>>) expressions[0]).getSingle();
            key = classInfo.getCodeName();
            if (isSingle) {
                aClass = classInfo.getC();
            } else {
                aClass = Array.newInstance(classInfo.getC(), 0).getClass();
            }
            String classname = classInfo.getC().getSimpleName();
            Logging.debug(this, "Class simple name: " + classname);
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
            aClass = Optional.ofNullable(converter.type).orElse(aClass);
            Logging.debug(this, "Converter to PLib type: " + key + ", aClass = " + aClass);
            return true;
        }
        try {
            Method method = PacketContainer.class.getMethod("get" + methodGetName);
            Logging.debug(this, "Method: " + method.toString() + ", aClass = " + aClass);
            converter = createConverter(method);
            if (converter == null) {
                Skript.error(key + " cannot be used in your version of Minecraft for the '%type/string% pinfo [array] %number% of %packet% expression");
                return false;
            }
            return true;
        } catch (NoSuchMethodException e) {
            Logging.debug(this, e);
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
            return CollectionUtils.array(aClass);
        }
        return null;
    }
}
