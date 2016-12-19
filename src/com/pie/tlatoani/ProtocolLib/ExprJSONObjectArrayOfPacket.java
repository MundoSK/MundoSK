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
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 5/8/16.
 */
public class ExprJSONObjectArrayOfPacket extends SimpleExpression<JSONObject> {
    private Method getObjects = null;
    private PacketInfoGetter getFunction;
    private PacketInfoSetter setFunction;
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private Class aClass;

    public static Map<String, PacketInfoGetter> getFunctionMap = new HashMap<String, PacketInfoGetter>();
    public static Map<String, PacketInfoSetter> setFunctionMap = new HashMap<String, PacketInfoSetter>();

    static {
        //Getters
        /*
        getFunctionMap.put("playerinfodata", new PacketInfoGetter() {
            @Override
            public JsonObject[] apply(PacketContainer packet, Integer index) {
                List<PlayerInfoData> playerInfoDatas = packet.getPlayerInfoDataLists().readSafely(index);
                JsonObject[] result = new JsonObject[playerInfoDatas.size()];
                for (int i = 0; i < playerInfoDatas.size(); i++) {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    PlayerInfoData playerInfoData = playerInfoDatas.get(i);
                    builder.add("profile", playerInfoData.getProfile().getUUID().toString());
                    builder.add("latency", playerInfoData.getPing());
                    builder.add("gamemode", playerInfoData.getGameMode().toBukkit().name());
                    if (playerInfoData.getDisplayName() != null) {
                        StringReader stringReader = new StringReader(playerInfoData.getDisplayName().getJson());
                        JsonReader jsonReader = Json.createReader(stringReader);
                        JsonObject jsonObject = jsonReader.readObject();
                        builder.add("displayname", jsonObject);
                    }
                    result[i] = builder.build();
                }
                return result;
            }
        });
        */

        //Setters
        /*
        setFunctionMap.put("playerinfodata", new PacketInfoSetter() {
            @Override
            public void apply(PacketContainer packet, Integer index, JsonObject[] value) {
                List<PlayerInfoData> playerInfoDatas = packet.getPlayerInfoDataLists().readSafely(index);
                playerInfoDatas.clear();
                for (int i = 0; i < value.length; i++) {
                    JsonObject jsonObject = value[i];
                    WrappedGameProfile wrappedGameProfile = null;
                    try {
                        wrappedGameProfile = WrappedGameProfile.fromOfflinePlayer(Bukkit.getServer().getOfflinePlayer(UUID.fromString(jsonObject.getString("profile"))));
                    } catch (IllegalArgumentException e) {}
                    int latency = jsonObject.getInt("latency");
                    EnumWrappers.NativeGameMode nativeGameMode = null;
                    try {
                        nativeGameMode = EnumWrappers.NativeGameMode.fromBukkit(GameMode.valueOf(jsonObject.getString("gamemode").toUpperCase()));
                    } catch (IllegalArgumentException e) {}
                    WrappedChatComponent chatComponent = null;
                    if (jsonObject.containsKey("displayname")) {
                        chatComponent = WrappedChatComponent.fromJson(jsonObject.getJsonObject("displayname").toString());
                    }
                    playerInfoDatas.add(new PlayerInfoData(wrappedGameProfile, latency, nativeGameMode, chatComponent));
                }
            }
        });
        */
    }

    @FunctionalInterface
    public interface PacketInfoGetter {
        public JSONObject[] apply(PacketContainer packet, Integer index);

    }

    @FunctionalInterface
    public interface PacketInfoSetter {
        public void apply(PacketContainer packet, Integer index, JSONObject[] value);

    }

    @Override
    protected JSONObject[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        Mundo.debug(this, "Packet before calling function :" + packet);
        JSONObject[] result = getFunction.apply(packet, index.getSingle(event).intValue());
        return result;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<JSONObject> getReturnType() {
        return JSONObject.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%string% array pjson %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        String string;
        if (expressions[0] instanceof Literal<?>) {
            string = ((Literal<String>) expressions[0]).getSingle();
        } else if (expressions[0] instanceof VariableString) {
            String fullstring = ((VariableString) expressions[0]).toString();
            string = fullstring.substring(1, fullstring.length() - 1);
        } else {
            Skript.error("The string '" + expressions[0] + "' is not a literal string! Only literal strings can be used in the pjson expression!");
            return false;
        }
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        if (getFunctionMap.containsKey(string.toLowerCase())) {
            getFunction = getFunctionMap.get(string.toLowerCase());
            setFunction = setFunctionMap.get(string.toLowerCase());
            return true;
        } else {
            Skript.error("The string " + string + " is not a valid packetinfo!");
            return false;
        }
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        PacketContainer packet = packetContainerExpression.getSingle(event);
        Mundo.debug(this, "Packet before calling function :" + packet);
        setFunction.apply(packet, index.getSingle(event).intValue(), ((JSONObject[]) delta));
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(JSONObject[].class);
        }
        return null;
    }
}