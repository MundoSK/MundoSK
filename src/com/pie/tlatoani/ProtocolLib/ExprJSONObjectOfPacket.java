package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.pie.tlatoani.JSON.JSONObject;
import org.bukkit.event.Event;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Tlatoani on 5/4/16.
 */
public class ExprJSONObjectOfPacket extends SimpleExpression<JSONObject> {
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
        getFunctionMap.put("chatcomponent", new PacketInfoGetter() {
            @Override
            public JSONObject apply(PacketContainer packet, Integer index) {
                WrappedChatComponent chatComponent = packet.getChatComponents().readSafely(index);
                String fromjson = chatComponent.getJson();
                JSONObject tojson = new JSONObject(fromjson);
                return tojson;
            }
        });

        //Setters
        setFunctionMap.put("chatcomponent", new PacketInfoSetter() {
            @Override
            public void apply(PacketContainer packet, Integer index, JSONObject value) {
                WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(value.toString());
                packet.getChatComponents().writeSafely(index, chatComponent);
            }
        });
    }

    @FunctionalInterface
    public interface PacketInfoGetter {
        public JSONObject apply(PacketContainer packet, Integer index);

    }

    @FunctionalInterface
    public interface PacketInfoSetter {
        public void apply(PacketContainer packet, Integer index, JSONObject value);

    }

    @Override
    protected JSONObject[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        JSONObject result = getFunction.apply(packet, index.getSingle(event).intValue());
        return new JSONObject[]{result};
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
        return "%string% pjson %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        String string = ((Literal<String>) expressions[0]).getSingle();
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
        setFunction.apply(packet, index.getSingle(event).intValue(), ((JSONObject) delta[0]));
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(JSONObject.class);
        }
        return null;
    }
}