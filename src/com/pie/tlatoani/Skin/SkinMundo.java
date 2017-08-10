package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Skin.MineSkin.*;
import com.pie.tlatoani.Util.Registration;
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
                fields.putObject("value", skin.toString());
                return fields;
            }

            @Override
            public void deserialize(Skin skin, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException("Skin does not have a nullary constructor!");
            }

            @Override
            public Skin deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                try {
                    Object parsedObject = new JSONParser().parse((String) fields.getObject("value"));
                    JSONObject jsonObject;
                    if (parsedObject instanceof JSONObject) {
                        jsonObject = (JSONObject) parsedObject;
                    } else {
                        jsonObject = (JSONObject) ((JSONArray) parsedObject).get(0);
                    }
                    return Skin.fromJSON(jsonObject);
                } catch (ParseException | ClassCastException e) {
                    throw new StreamCorruptedException();
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
        //Registration.registerEffect(EffTestMineSkin.class, "test mineskin %string%");
        Registration.registerExpression(ExprSkinWith.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] (with|of) value %string% signature %string%");
        Registration.registerExpression(ExprSkinOf.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] of %player/itemstack%", "%player/itemstack%'s skin");
        Registration.registerExpression(ExprCombinedSkin.class, Skin.class, ExpressionType.PROPERTY, "(combined skin|skin combination) (from|of) %skins%", "%skins%'s (combined skin|skin combination)");
        Registration.registerExpression(ExprDisplayedSkinOfPlayer.class, Skin.class, ExpressionType.PROPERTY, "displayed skin of %player% [(for %-players%|excluding %-players%)]", "%player%'s displayed skin [(for %-players%|excluding %-players%)]");
        Registration.registerExpression(ExprSkullFromSkin.class, ItemStack.class, ExpressionType.PROPERTY, "skull from %skin%");
        Registration.registerExpression(ExprRetrievedSkin.class, Skin.class, ExpressionType.PROPERTY, "retrieved skin (from (0¦file|1¦url) %-string%|2¦of %-offlineplayer%)");
        Registration.registerExpression(ExprNameTagOfPlayer.class, String.class, ExpressionType.PROPERTY, "%player%'s name[]tag", "name[]tag of %player%");
    }
}
