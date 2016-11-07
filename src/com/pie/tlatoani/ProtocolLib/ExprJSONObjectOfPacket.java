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
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;
import com.pie.tlatoani.Mundo;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 5/4/16.
 */
public class ExprJsonObjectOfPacket extends SimpleExpression<JSONObject> {
    private PacketInfoConverter<JSONObject> converter;
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;

    public static Map<String, PacketInfoConverter<JSONObject>> converterMap = new HashMap<>();

    static {

        //Converters

        registerConverter("chatcomponent", new PacketInfoConverter<JSONObject>() {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                Mundo.debug(this, "Packet :" + packet);
                Mundo.debug(this, "ChatComponents :" + packet.getChatComponents());
                WrappedChatComponent chatComponent = packet.getChatComponents().readSafely(index);
                String fromjson = chatComponent.getJson();
                Mundo.debug(this, "Fromjson: " + fromjson);
                JSONParser parser = new JSONParser();
                JSONObject tojson = null;
                try {
                    tojson = (JSONObject) parser.parse(fromjson);
                } catch (ParseException | ClassCastException e) {
                    Mundo.debug(ExprJsonObjectOfPacket.class, e);
                }
                Mundo.debug(this, "Tojson " + tojson);
                return tojson;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(value.toString());
                packet.getChatComponents().writeSafely(index, chatComponent);
            }
        });

        registerConverter("serverping", new PacketInfoConverter<JSONObject>() {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                try {
                    return (JSONObject) (new JSONParser()).parse(packet.getServerPings().readSafely(0).toJson());
                } catch (ParseException | ClassCastException e) {
                    Mundo.reportException(ExprJsonObjectOfPacket.class, e);
                    return null;
                }
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                packet.getServerPings().writeSafely(0, WrappedServerPing.fromJson(value.toJSONString()));
            }
        });

        registerConverter("datawatcher", new PacketInfoConverter<JSONObject>() {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                JSONObject jsonObject = new JSONObject();
                WrappedDataWatcher dataWatcher = packet.getDataWatcherModifier().readSafely(index);
                jsonObject.put("entity", dataWatcher.getEntity());
                if (dataWatcher != null) {
                    dataWatcher.forEach(new Consumer<WrappedWatchableObject>() {
                        int i = 0;

                        @Override
                        public void accept(WrappedWatchableObject wrappedWatchableObject) {
                            jsonObject.put("" + i, wrappedWatchableObject.getValue());
                            i++;
                        }
                    });
                }
                return jsonObject;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                List<WrappedWatchableObject> wrappedWatchableObjects = new ArrayList<WrappedWatchableObject>();
                Entity entity = (Entity) value.get("entity");
                value.forEach(new BiConsumer() {
                    @Override
                    public void accept(Object o, Object o2) {
                        try {
                            String key = (String) o;
                            int i = Integer.parseInt(key);
                            WrappedWatchableObject watchableObject = new WrappedWatchableObject(i, o2);
                            wrappedWatchableObjects.add(watchableObject);

                        } catch (ClassCastException | NumberFormatException e) {}
                    }
                });
                WrappedDataWatcher dataWatcher = new WrappedDataWatcher(wrappedWatchableObjects);
                dataWatcher.setEntity(entity);
                packet.getDataWatcherModifier().writeSafely(index, dataWatcher);
            }
        });

        registerConverter("watchablecollection", new PacketInfoConverter<JSONObject>() {
            @Override
            public JSONObject get(PacketContainer packet, Integer index) {
                JSONObject jsonObject = new JSONObject();
                Collection<WrappedWatchableObject> wrappedWatchableObjects = packet.getWatchableCollectionModifier().readSafely(index);
                if (wrappedWatchableObjects != null) {
                    wrappedWatchableObjects.forEach(new Consumer<WrappedWatchableObject>() {
                        int i = 0;

                        @Override
                        public void accept(WrappedWatchableObject wrappedWatchableObject) {
                            jsonObject.put("" + i, wrappedWatchableObject.getValue());
                            i++;
                        }
                    });
                }
                return jsonObject;
            }

            @Override
            public void set(PacketContainer packet, Integer index, JSONObject value) {
                List<WrappedWatchableObject> wrappedWatchableObjects = new ArrayList<WrappedWatchableObject>();
                value.forEach(new BiConsumer() {
                    @Override
                    public void accept(Object o, Object o2) {
                        try {
                            String key = (String) o;
                            int i = Integer.parseInt(key);
                            WrappedWatchableObject watchableObject = new WrappedWatchableObject(i, o2);
                            wrappedWatchableObjects.add(watchableObject);

                        } catch (ClassCastException | NumberFormatException e) {}
                    }
                });
                packet.getWatchableCollectionModifier().writeSafely(index, wrappedWatchableObjects);
            }
        });
    }

    public static void registerConverter(String name, PacketInfoConverter<JSONObject> converter) {
        converterMap.put(name, converter);
    }

    public static PacketInfoConverter<JSONObject> getConverter(String name) {
        return converterMap.get(name);
    }

    @Override
    protected JSONObject[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        Mundo.debug(this, "Packet before calling function :" + packet);
        JSONObject result = converter.get(packet, index.getSingle(event).intValue());
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
        converter = getConverter(string.toLowerCase());
        if (converter == null) {
            Skript.error("The string " + string + " is not a valid packetinfo!");
            return false;
        }
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        PacketContainer packet = packetContainerExpression.getSingle(event);
        Mundo.debug(this, "Packet before calling function :" + packet);
        converter.set(packet, index.getSingle(event).intValue(), ((JSONObject) delta[0]));
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(JSONObject.class);
        }
        return null;
    }
}