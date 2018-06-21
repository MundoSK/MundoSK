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
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.nbt.*;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

/**
 * Created by Tlatoani on 5/4/16.
 */
public class ExprJSONObjectOfPacket extends SimpleExpression<JSONObject> {
    private PacketInfoConverter<JSONObject> singleConverter = null;
    private PacketInfoConverter<JSONObject[]> pluralConverter = null;
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private boolean isSingle;

    public static Map<String, PacketInfoConverter<JSONObject>> singleConverters = new LinkedHashMap<>();
    public static Map<String, PacketInfoConverter<JSONObject[]>> pluralConverters = new LinkedHashMap<>();

    public abstract static class PacketJSONConverter extends PacketInfoConverter<JSONObject> {

        protected PacketJSONConverter() {
            super(JSONObject.class);
        }
    }

    public static JSONObject fromNBTBase(NbtBase nbtBase) {
        if (nbtBase == null) {
            return null;
        }
        JSONObject result = new JSONObject();
        result.put("name", nbtBase.getName());
        if (nbtBase.getType() == NbtType.TAG_LIST) {
            result.put("type", "list_" + ((NbtList) nbtBase).getElementType().toString().substring(4).toLowerCase());
        } else {
            result.put("type", nbtBase.getType().toString().substring(4).toLowerCase());
        }
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
                JSONArray jsonByteArray = new JSONArray();
                for (byte b : (byte[]) nbtBase.getValue()) {
                    jsonByteArray.add(b);
                }
                result.put("value", jsonByteArray);
                return result;
            case TAG_INT_ARRAY:
                JSONArray jsonIntArray = new JSONArray();
                for (int i : (int[]) nbtBase.getValue()) {
                    jsonIntArray.add(i);
                }
                result.put("value", jsonIntArray);
                return result;
            case TAG_LIST:
                JSONArray jsonArray = new JSONArray();
                for (Object elem : (NbtList) nbtBase) {
                    jsonArray.add(elem);
                }
                result.put("value", jsonArray);
                return result;
            case TAG_COMPOUND:
                JSONObject jsonObject = new JSONObject();
                for (NbtBase member : (NbtCompound) nbtBase) {
                    if (member.getType() == NbtType.TAG_END) continue;
                    jsonObject.put(member.getName(), fromNBTBase(member));
                }
                result.put("value", jsonObject);
                return result;
        }
        return null;
    }

    public static NbtBase toNBTBase(String name, String typeName, Object value) {
        NbtType type;
        String elemTypeName;
        if (typeName.startsWith("list")) {
            type = NbtType.TAG_LIST;
            elemTypeName = typeName.substring(5);
        } else {
            type = NbtType.valueOf("TAG_" + typeName.toUpperCase());
            elemTypeName = null;
        }
        Number number = value instanceof Number ? (Number) value : null;
        JSONArray jsonArray = value instanceof JSONArray ? (JSONArray) value : null;
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
                return NbtFactory.of(name, (String) value);
            case TAG_BYTE_ARRAY:
                byte[] bytes = new byte[jsonArray.size()];
                for (int i = 0; i < bytes.length; i++) {
                    bytes[i] = ((Number) jsonArray.get(i)).byteValue();
                }
                return NbtFactory.of(name, bytes);
            case TAG_INT_ARRAY:
                int[] ints = new int[jsonArray.size()];
                for (int i = 0; i < ints.length; i++) {
                    ints[i] = ((Number) jsonArray.get(i)).intValue();
                }
                return NbtFactory.of(name, ints);
            case TAG_LIST:
                NbtBase[] nbtBases = new NbtBase[jsonArray.size()];
                for (int i = 0; i < nbtBases.length; i++) {
                    nbtBases[i] = toNBTBase(NbtList.EMPTY_NAME, elemTypeName, jsonArray.get(i));
                }
                return NbtFactory.ofList(name, nbtBases);
            case TAG_COMPOUND:
                NbtCompound nbtCompound = NbtFactory.ofCompound(name);
                ((JSONObject) value).forEach((__, maybeJSONObject) -> {
                    JSONObject jsonObject = (JSONObject) maybeJSONObject;
                    String name1 = (String) jsonObject.get("name");
                    String typeName1 = (String) jsonObject.get("type");
                    Object value1 = jsonObject.get("value");
                    nbtCompound.put(toNBTBase(name1, typeName1, value1));
                });
                return nbtCompound;
        }
        throw new IllegalArgumentException("Illegal NbtType: " + type);
    }

    public static WrappedDataWatcher.Serializer getSerializer(Class c) {
        try {
            WrappedDataWatcher.Serializer serializer = Registry.get(c);
            if (serializer != null) {
                return serializer;
            }
            if (WrappedChatComponent.class.isAssignableFrom(c)) {
                return Registry.getChatComponentSerializer();
            } else if (ItemStack.class.isAssignableFrom(c)) {
                return Registry.getItemStackSerializer(false);
            } else if (WrappedBlockData.class.isAssignableFrom(c)) {
                return Registry.getBlockDataSerializer(false);
            } else if (Vector3F.class.isAssignableFrom(c)) {
                return Registry.getVectorSerializer();
            } else if (BlockPosition.class.isAssignableFrom(c)) {
                return Registry.getBlockPositionSerializer(false);
            } else if (EnumWrappers.Direction.class == c) {
                return Registry.getDirectionSerializer();
            } else if (NbtCompound.class.isAssignableFrom(c)) {
                return Registry.getNBTCompoundSerializer();
            }
            return null;
        } catch (RuntimeException e) {
            Logging.debug(ExprJSONObjectOfPacket.class, e);
        }
        return null;
    }

    public static JSONObject fromWatchableCollection(Collection<WrappedWatchableObject> watchableObjects) {
        JSONObject jsonObject = new JSONObject();
        for (WrappedWatchableObject wrappedWatchableObject : watchableObjects) {
            jsonObject.put("" + wrappedWatchableObject.getIndex(), wrappedWatchableObject.getValue());
        }
        return jsonObject;
    }

    public static WrappedDataWatcher toDataWatcher(JSONObject jsonObject) {
        WrappedDataWatcher dataWatcher;
        Object maybeEntity = jsonObject.get("entity");
        if (maybeEntity instanceof Entity) {
            dataWatcher = new WrappedDataWatcher((Entity) maybeEntity);
        } else {
            dataWatcher = new WrappedDataWatcher();
        }
        jsonObject.forEach((key, value) -> {
            try {
                String keyStr = (String) key;
                int i = Integer.parseInt(keyStr);
                Logging.debug(ExprJSONObjectOfPacket.class, "i = " + i + ", value = " + value + ", value.getClass() = " + value.getClass());
                WrappedDataWatcher.Serializer serializer = getSerializer(value.getClass());
                Logging.debug(ExprJSONObjectOfPacket.class, "serializer = " + serializer);
                if (serializer == null) {
                    dataWatcher.setObject(i, value);
                } else {
                    dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(i, serializer), value);
                }
            } catch (ClassCastException | IllegalArgumentException e) {
                Logging.debug(ExprJSONObjectOfPacket.class, e);
            }
        });
        return dataWatcher;
    }

    public static JSONObject fromGameProfile(WrappedGameProfile gameProfile) {
        JSONObject result = new JSONObject();
        result.put("name", gameProfile.getName());
        result.put("uuid", gameProfile.getUUID().toString());
        result.put("skin", Skin.fromGameProfile(gameProfile));
        return result;
    }

    public static WrappedGameProfile toGameProfile(JSONObject value) {
        WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.fromString((String) value.get("uuid")), (String) value.get("name"));
        gameProfile.getProperties().put(Skin.MULTIMAP_KEY, ((Skin) value.get("skin")).toWrappedSignedProperty());
        return gameProfile;
    }

    static void registerConverters() {

        //Single Converters

        registerSingleConverter("chatcomponent", new PacketInfoConverter<JSONObject>() {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                WrappedChatComponent chatComponent = packet.getChatComponents().readSafely(index);
                Logging.debug(ExprJSONObjectOfPacket.class, "ChatComponent: " + chatComponent);
                if (chatComponent == null) {
                    return null;
                }
                String fromJson = chatComponent.getJson();
                Logging.debug(ExprJSONObjectOfPacket.class,"FromJson: " + fromJson);
                JSONParser parser = new JSONParser();
                JSONObject toJson = null;
                try {
                    Object parsedJson = parser.parse(fromJson);
                    if (parsedJson instanceof JSONObject) {
                        toJson = (JSONObject) parsedJson;
                    } else if (parsedJson instanceof String) {
                        toJson = new JSONObject();
                        toJson.put("text", parsedJson);
                    } else {
                        throw new IllegalStateException("The json: " + fromJson + "; is neither a jsonobject nor a string");
                    }
                } catch (ParseException | IllegalStateException e) {
                    Logging.debug(ExprJSONObjectOfPacket.class, e);
                }
                return toJson;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(value.toString());
                packet.getChatComponents().writeSafely(index, chatComponent);
            }
        });

        registerSingleConverter("serverping", new PacketInfoConverter<JSONObject>(JSONObject.class) {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                try {
                    WrappedServerPing serverPing = packet.getServerPings().readSafely(0);
                    if (serverPing == null) {
                        return null;
                    }
                    return (JSONObject) (new JSONParser()).parse(serverPing.toJson());
                } catch (ParseException | ClassCastException e) {
                    Logging.reportException(ExprJSONObjectOfPacket.class, e);
                    return null;
                }
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                packet.getServerPings().writeSafely(0, WrappedServerPing.fromJson(value.toJSONString()));
            }
        });



        registerSingleConverter("datawatcher", new PacketInfoConverter<JSONObject>(JSONObject.class) {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                WrappedDataWatcher dataWatcher = packet.getDataWatcherModifier().readSafely(index);
                if (dataWatcher == null) {
                    return null;
                }
                JSONObject jsonObject = fromWatchableCollection(dataWatcher.getWatchableObjects());
                jsonObject.put("entity", dataWatcher.getEntity());
                return jsonObject;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                packet.getDataWatcherModifier().writeSafely(index, toDataWatcher(value));
            }
        });

        registerSingleConverter("watchablecollection", new PacketInfoConverter<JSONObject>(JSONObject.class) {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                Collection<WrappedWatchableObject> wrappedWatchableObjects = packet.getWatchableCollectionModifier().readSafely(index);
                if (wrappedWatchableObjects == null) {
                    return null;
                }
                return fromWatchableCollection(wrappedWatchableObjects);
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                packet.getWatchableCollectionModifier().writeSafely(index, toDataWatcher(value).getWatchableObjects());
            }
        });

        registerSingleConverter("gameprofile", new PacketInfoConverter<JSONObject>(JSONObject.class) {

            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                return fromGameProfile(packet.getGameProfiles().readSafely(index));
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                packet.getGameProfiles().writeSafely(index, toGameProfile(value));
            }
        });

        registerSingleConverter("nbt", new PacketJSONConverter() {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                return fromNBTBase(packet.getNbtModifier().readSafely(index));
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                try {
                    String name1 = (String) value.get("name");
                    String typeName1 = (String) value.get("type");
                    Object value1 = value.get("value");
                    packet.getNbtModifier().writeSafely(index, toNBTBase(name1, typeName1, value1));
                } catch (ClassCastException | IllegalArgumentException | NullPointerException e) {
                    Logging.debug(ExprJSONObjectOfPacket.class, e);
                }
            }
        });

        //Plural Converters

        registerPluralConverter("chatcomponent", new PacketInfoConverter<JSONObject[]>(JSONObject[].class) {
            @Override
            public JSONObject[] get(PacketContainer packet, Integer index) {
                WrappedChatComponent[] chatComponents = packet.getChatComponentArrays().readSafely(index);
                if (chatComponents == null) {
                    return null;
                }
                JSONObject[] result = new JSONObject[chatComponents.length];
                for (int i = 0; i < chatComponents.length; i++) {
                    WrappedChatComponent chatComponent = chatComponents[i];
                    String fromjson = chatComponent.getJson();
                    JSONParser parser = new JSONParser();
                    JSONObject tojson = null;
                    try {
                        tojson = (JSONObject) parser.parse(fromjson);
                    } catch (ParseException | ClassCastException e) {
                        Logging.debug(ExprJSONObjectOfPacket.class, e);
                    }
                    result[i] = tojson;
                }
                return result;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject[] value) {
                WrappedChatComponent[] result = new WrappedChatComponent[value.length];
                for (int i = 0; i < value.length; i++) {
                    result[i] = WrappedChatComponent.fromJson(value[i].toJSONString());
                }
                packet.getChatComponentArrays().writeSafely(index, result);
            }
        });

        registerPluralConverter("playerinfodata", new PacketInfoConverter<JSONObject[]>(JSONObject[].class) {
            @Override
            public JSONObject[] get(PacketContainer packet, Integer index) {
                try {
                    List<PlayerInfoData> playerInfoDatas = packet.getPlayerInfoDataLists().readSafely(index);
                    if (playerInfoDatas == null) {
                        return null;
                    }
                    JSONObject[] result = new JSONObject[playerInfoDatas.size()];
                    for (int i = 0; i < result.length; i++) {
                        PlayerInfoData playerInfoData = playerInfoDatas.get(i);
                        Logging.debug(ExprJSONObjectOfPacket.class, "PlayerInfoData: " + playerInfoData);
                        Logging.debug(ExprJSONObjectOfPacket.class, "PlayerInfoData.getDisplayName(): " + playerInfoData.getDisplayName());
                        result[i] = new JSONObject();
                        if (playerInfoData.getDisplayName() != null) {
                            result[i].put("displayname", (new JSONParser()).parse(playerInfoData.getDisplayName().getJson()));
                        }
                        result[i].put("gamemode", playerInfoData.getGameMode().toBukkit());
                        result[i].put("latency", playerInfoData.getLatency());
                        result[i].put("gameprofile", fromGameProfile(playerInfoData.getProfile()));
                    }
                    return result;
                } catch (ParseException e) {
                    Logging.reportException(ExprJSONObjectOfPacket.class, e);
                    return new JSONObject[0];
                }
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject[] value) {
                ArrayList<PlayerInfoData> playerInfoDatas = new ArrayList<PlayerInfoData>();
                for (JSONObject jsonObject : value) {
                    Object displayName = jsonObject.get("displayname");
                    playerInfoDatas.add(new PlayerInfoData(
                            toGameProfile((JSONObject) jsonObject.get("gameprofile")),
                            ((Number) jsonObject.get("latency")).intValue(),
                            EnumWrappers.NativeGameMode.fromBukkit((GameMode) jsonObject.get("gamemode")),
                            displayName == null ? null : WrappedChatComponent.fromJson(displayName.toString())
                    ));
                }
                packet.getPlayerInfoDataLists().writeSafely(index, playerInfoDatas);
            }
        });
    }

    public static void registerSingleConverter(String name, PacketInfoConverter<JSONObject> converter) {
        singleConverters.put(name, converter);
    }

    public static void registerPluralConverter(String name, PacketInfoConverter<JSONObject[]> converter) {
        pluralConverters.put(name, converter);
    }

    public static PacketInfoConverter<JSONObject> getSingleConverter(String name) {
        return singleConverters.get(name);
    }

    public static PacketInfoConverter<JSONObject[]> getPluralConverter(String name) {
        return pluralConverters.get(name);
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

    @Override
    protected JSONObject[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        Logging.debug(this, " ore calling function :" + packet);
        Logging.debug(this, "Converters == " + singleConverter + " , " + pluralConverter);
        return isSingle ? new JSONObject[]{singleConverter.get(packet, index)} : pluralConverter.get(packet, index);
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<JSONObject> getReturnType() {
        return JSONObject.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%string% pjson %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        String string;
        isSingle = i == 0;
        if (expressions[0] == null) {
            string = getConverterNameByIndex(parseResult.mark, isSingle);
        } else if (expressions[0] instanceof Literal<?>) {
            string = ((Literal<String>) expressions[0]).getSingle();
        } else if (expressions[0] instanceof VariableString) {
            String fullstring = ((VariableString) expressions[0]).toString();
            string = fullstring.substring(1, fullstring.length() - 1);
        } else {
            Skript.error("The string '" + expressions[0] + "' is not a literal string! Only literal strings can be used in the pjson expression!");
            return false;
        }
        Logging.debug(this, "String == " + string + " isSingle == " + isSingle);
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        singleConverter = getSingleConverter(string.toLowerCase());
        pluralConverter = getPluralConverter(string.toLowerCase());
        Logging.debug(this, "Converters == " + singleConverter + " , " + pluralConverter);
        if (isSingle ? singleConverter == null : pluralConverter == null) {
            Skript.error("The string " + string + " is not a valid packetinfo!");
            return false;
        }
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        PacketContainer packet = packetContainerExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        Logging.debug(this, "Packet before calling function :" + packet);
        if (isSingle) {
            singleConverter.set(packet, index, ((JSONObject) delta[0]));
        } else {
            JSONObject[] deltaJSON = new JSONObject[delta.length];
            for (int i = 0; i < delta.length; i++) {
                deltaJSON[i] = (JSONObject) delta[i];
            }
            pluralConverter.set(packet, index, deltaJSON);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            if (isSingle) {
                return CollectionUtils.array(JSONObject.class);
            } else {
                return CollectionUtils.array(JSONObject[].class);
            }
        }
        return null;
    }
}