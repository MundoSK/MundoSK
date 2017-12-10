package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.MineSkin.ExprRetrievedSkin;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Registration.Registration;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class SkinMundo {
    
    public static void load() {
        SkinManager.loadReflectionStuff();
        SkinManager.loadPacketEvents();

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event) {
                SkinManager.onJoin(event.getPlayer());
            }
        }, Mundo.INSTANCE);
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                SkinManager.onQuit(event.getPlayer());
            }
        }, Mundo.INSTANCE);

        Registration.registerType(Skin.class, "skin", "skintexture").parser(new Registration.SimpleParser<Skin>() {
            @Override
            public Skin parse(String s, ParseContext parseContext) {
                if (s.equalsIgnoreCase("STEVE")) {
                    return Skin.STEVE;
                }
                if (s.equalsIgnoreCase("ALEX")) {
                    return Skin.ALEX;
                }
                return null;
            }
        }).serializer(new Serializer<Skin>() {
            @Override
            public Fields serialize(Skin skin) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("value", skin.value);
                fields.putObject("signature", skin.signature);
                return fields;
            }

            @Override
            public void deserialize(Skin skin, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException("Skin does not have a nullary constructor!");
            }

            @Override
            public Skin deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                try {
                    String value = (String) fields.getObject("value");
                    String signature = (String) fields.getObject("signature");
                    Logging.debug(SkinMundo.class, "value: " + value + ", signature: " + signature);
                    return new Skin(value, signature);
                } catch (StreamCorruptedException | ClassCastException e) {
                    try {
                        String value = (String) fields.getObject("value");
                        Logging.debug(SkinMundo.class, "value: " + value);
                        Object parsedObject = new JSONParser().parse(value);
                        Logging.debug(SkinMundo.class, "parsedobject: " + parsedObject);
                        JSONObject jsonObject;
                        if (parsedObject instanceof JSONObject) {
                            jsonObject = (JSONObject) parsedObject;
                        } else {
                            jsonObject = (JSONObject) ((JSONArray) parsedObject).get(0);
                        }
                        return Skin.fromJSON(jsonObject);
                    } catch (ParseException | ClassCastException e1) {
                        throw new StreamCorruptedException();
                    }
                }
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }
        });
        Registration.registerExpression(ExprSkinWith.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] (with|of) value %string% signature %string%");
        Registration.registerExpression(ExprSkinOf.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] of %player/itemstack%", "%player/itemstack%'s skin");
        Registration.registerExpression(ExprDisplayedSkinOfPlayer.class, Skin.class, ExpressionType.PROPERTY, "displayed skin of %player% [(for %-players%|excluding %-players%)]", "%player%'s displayed skin [(for %-players%|excluding %-players%)]");
        Registration.registerExpression(ExprSkullFromSkin.class, ItemStack.class, ExpressionType.PROPERTY, "skull from %skin%");
        Registration.registerExpression(ExprRetrievedSkin.class, Skin.class, ExpressionType.PROPERTY, "retrieved skin (from (0¦file|1¦url) %-string%|2¦of %-offlineplayer%) [[with] timeout %-timespan%]");
        Registration.registerExpression(ExprNameTagOfPlayer.class, String.class, ExpressionType.PROPERTY, "[mundo[sk]] %player%'s name[]tag", "[mundo[sk]] name[]tag of %player%");
        Registration.registerExpression(ExprTabName.class, String.class, ExpressionType.PROPERTY, "%player%'s [mundo[sk]] tab[list] name", "[mundo[sk]] tab[list] name of %player%");
    }
}
