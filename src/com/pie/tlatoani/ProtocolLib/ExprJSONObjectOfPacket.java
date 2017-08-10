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
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Util.Logging;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
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
                List<WrappedWatchableObject> wrappedWatchableObjects = new ArrayList<WrappedWatchableObject>();
                value.forEach((keyO, valueO) -> {
                    try {
                        String key = (String) keyO;
                        int i = Integer.parseInt(key);
                        Logging.debug(ExprJSONObjectOfPacket.class, "WrappedWatchableObject creation, Index = " + i + ", Value = " + valueO);
                        WrappedWatchableObject watchableObject = new WrappedWatchableObject(i, valueO);
                        wrappedWatchableObjects.add(watchableObject);
                    } catch (ClassCastException | NumberFormatException e) {
                        Logging.debug(ExprJSONObjectOfPacket.class, e);
                    }
                });
                packet.getWatchableCollectionModifier().writeSafely(index, wrappedWatchableObjects);
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