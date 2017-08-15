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
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Util.Logging;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.function.Function;

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
        result.put("type", nbtBase.getType().toString().substring(4).toLowerCase());
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
                for (NbtBase base : (List<NbtBase>) nbtBase.getValue()) {
                    jsonArray.add(fromNBTBase(base));
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

    public static NbtBase toNBTBase(JSONObject value) {
        try {
            String name = (String) value.get("name");
            String typeName = (String) value.get("type");
            NbtType type = NbtType.valueOf("TAG_" + typeName.toUpperCase());
            Object rawValue = value.get("value");
            Number number = rawValue instanceof Number ? (Number) rawValue : null;
            JSONArray jsonArray = rawValue instanceof JSONArray ? (JSONArray) rawValue : null;
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
                    return NbtFactory.of(name, (String) rawValue);
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
                        nbtBases[i] = toNBTBase((JSONObject) jsonArray.get(i));
                    }
                    return NbtFactory.ofList(name, nbtBases);
                case TAG_COMPOUND:
                    NbtCompound nbtCompound = NbtFactory.ofCompound(name);
                    ((JSONObject) rawValue).forEach((__, value1) -> nbtCompound.put(toNBTBase((JSONObject) value1)));
                    return nbtCompound;
            }
            throw new IllegalArgumentException("Illegal NbtType: " + type);
        } catch (ClassCastException | NullPointerException | IllegalArgumentException e) {
            Logging.debug(ExprJSONObjectOfPacket.class, e);
            return null;
        }
    }

    static {

        //Converters

        //Single

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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("entity", dataWatcher.getEntity());
                if (dataWatcher != null) {
                    for (WrappedWatchableObject wrappedWatchableObject : dataWatcher) {
                        jsonObject.put(wrappedWatchableObject.getIndex() + "", wrappedWatchableObject.getValue());
                    }
                }
                return jsonObject;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                List<WrappedWatchableObject> wrappedWatchableObjects = new ArrayList<WrappedWatchableObject>();
                Entity entity = (Entity) value.get("entity");
                value.forEach((keyO, valueO) -> {
                    try {
                        String key = (String) keyO;
                        int i = Integer.parseInt(key);
                        WrappedWatchableObject watchableObject = new WrappedWatchableObject(i, valueO);
                        wrappedWatchableObjects.add(watchableObject);
                    } catch (ClassCastException | NumberFormatException e) {
                        Logging.debug(ExprJSONObjectOfPacket.class, e);
                    }
                });
                WrappedDataWatcher dataWatcher = new WrappedDataWatcher(wrappedWatchableObjects);
                dataWatcher.setEntity(entity);
                packet.getDataWatcherModifier().writeSafely(index, dataWatcher);
            }
        });

        registerSingleConverter("watchablecollection", new PacketInfoConverter<JSONObject>(JSONObject.class) {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                Collection<WrappedWatchableObject> wrappedWatchableObjects = packet.getWatchableCollectionModifier().readSafely(index);
                if (wrappedWatchableObjects == null) {
                    return null;
                }
                JSONObject jsonObject = new JSONObject();
                for (WrappedWatchableObject wrappedWatchableObject : wrappedWatchableObjects) {
                    Logging.debug(ExprJSONObjectOfPacket.class, "WrappedWatchableObject, Index = " + wrappedWatchableObject.getIndex() + ", Value = " + wrappedWatchableObject.getValue() + ", WDWO = " + wrappedWatchableObject.getWatcherObject());
                    jsonObject.put("" + wrappedWatchableObject.getIndex(), wrappedWatchableObject.getValue());
                }
                return jsonObject;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
                value.forEach((keyO, valueO) -> {
                    try {
                        String key = (String) keyO;
                        int i = Integer.parseInt(key);
                        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(i, WrappedDataWatcher.Registry.get(valueO.getClass())), valueO);
                    } catch (ClassCastException | NumberFormatException e) {
                        Logging.debug(ExprJSONObjectOfPacket.class, e);
                    }
                });
                packet.getWatchableCollectionModifier().writeSafely(index, dataWatcher.getWatchableObjects());
                /*List<WrappedWatchableObject> wrappedWatchableObjects = new ArrayList<WrappedWatchableObject>();
                value.forEach((keyO, valueO) -> {
                    try {
                        String key = (String) keyO;
                        int i = Integer.parseInt(key);
                        Logging.debug(ExprJSONObjectOfPacket.class, "WrappedWatchableObject creation, Index = " + i + ", Value = " + valueO);
                        WrappedWatchableObject watchableObject = new WrappedWatchableObject(i, valueO);
                        Logging.debug(ExprJSONObjectOfPacket.class, "WrappedWatchableObject created, Index = " + watchableObject.getIndex() + ", Value = " + watchableObject.getValue() + ", WDWO = " + watchableObject.getWatcherObject());
                        wrappedWatchableObjects.add(watchableObject);
                    } catch (ClassCastException | NumberFormatException e) {
                        Logging.debug(ExprJSONObjectOfPacket.class, e);
                    }
                });
                packet.getWatchableCollectionModifier().writeSafely(index, wrappedWatchableObjects);*/
            }
        });

        Function<WrappedGameProfile, JSONObject> gameProfileToJSON = new Function<WrappedGameProfile, JSONObject>() {
            @Override
            public JSONObject apply(WrappedGameProfile gameProfile) {
                JSONObject result = new JSONObject();
                result.put("name", gameProfile.getName());
                result.put("uuid", gameProfile.getUUID());
                result.put("skin", Skin.fromGameProfile(gameProfile));
                return result;
            }
        };
        Function<JSONObject, WrappedGameProfile> gameProfileFromJSON = new Function<JSONObject, WrappedGameProfile>() {
            @Override
            public WrappedGameProfile apply(JSONObject value) {
                WrappedGameProfile gameProfile = new WrappedGameProfile(UUID.fromString((String) value.get("uuid")), (String) value.get("name"));
                gameProfile.getProperties().put(Skin.MULTIMAP_KEY, ((Skin) value.get("skin")).toWrappedSignedProperty());
                return gameProfile;
            }
        };

        registerSingleConverter("gameprofile", new PacketInfoConverter<JSONObject>(JSONObject.class) {

            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                return gameProfileToJSON.apply(packet.getGameProfiles().readSafely(index));
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                packet.getGameProfiles().writeSafely(index, gameProfileFromJSON.apply(value));
            }
        });

        registerSingleConverter("nbt", new PacketJSONConverter() {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                return fromNBTBase(packet.getNbtModifier().readSafely(index));
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                packet.getNbtModifier().writeSafely(index, toNBTBase(value));
            }
        });

        //Plural

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
                        result[i].put("gameprofile", gameProfileToJSON.apply(playerInfoData.getProfile()));
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
                            gameProfileFromJSON.apply((JSONObject) jsonObject.get("gameprofile")),
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
            return CollectionUtils.array(JSONObject.class);
        }
        return null;
    }
}