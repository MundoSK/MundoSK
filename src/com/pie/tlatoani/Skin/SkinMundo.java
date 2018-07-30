package com.pie.tlatoani.Skin;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Core.Registration.Registration;
import com.pie.tlatoani.Skin.Retrieval.EffRetrieveSkin;
import com.pie.tlatoani.Skin.Retrieval.ExprRetrievedSkin;
import com.pie.tlatoani.Skin.Skull.*;
import com.pie.tlatoani.Core.Static.Logging;
import org.bukkit.Bukkit;
import org.bukkit.SkullType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.UUID;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class SkinMundo {
    
    public static void load() {
        ProfileManager.loadReflectionStuff();
        ProfileManager.loadPacketEvents();

        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                ProfileManager.onQuit(event.getPlayer());
            }
        }, Mundo.get());

        Registration.registerType(Skin.class, "skin", "skintexture")
                .document("Skin Texture", "1.8", "Represents a skin, possibly of a player. Write 'steve' or 'alex' for these respective skins.")
                .example("skin with name \"eyJ0aW1lc3RhbXAiOjE0NzQyMTc3NjkwMDAsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE5MmI0NTY2ZjlhMjg2OTNlNGMyNGFiMTQxNzJjZDM0MjdiNzJiZGE4ZjM0ZDRhNjEwODM3YTQ3ZGEwZGUifX19\" signature \"pRQbSEnKkNmi0uW7r8H4xzoWS3E4tkWNbiwwRYgmvITr0xHWSKii69TcaYDoDBXGBwZ525Ex5z5lYe5Xg6zb7pyBPiTJj8J0QdKenQefVnm6Vi1SAR1uN131sRddgK2Gpb2z0ffsR9USDjJAPQtQwCqz0M7sHeXUJhuRxnbznpuZwGq+B34f1TqyVH8rcOSQW9zd+RY/MEUuIHxmSRZlfFIwYVtMCEmv4SbhjLNIooGp3z0CWqDhA7GlJcDFb64FlsJyxrAGnAsUwL2ocoikyIQceyj+TVyGIEuMIpdEifO6+NkCnV7v+zTmcutOfA7kHlj4d1e5ylwi3/3k4VKZhINyFRE8M8gnLgbVxNZ4mNtI3ZMWmtmBnl9dVujyo+5g+vceIj5Admq6TOE0hy7XoDVifLWyNwO/kSlXl34ZDq1MCVN9f1ryj4aN7BB8/Tb2M4sJf3YoGi0co0Hz/A4y14M5JriG21lngw/vi5Pg90GFz64ASssWDN9gwuf5xPLUHvADGo0Bue8KPZPyI0iuIi/3sZCQrMcdyVcur+facIObTQhMut71h8xFeU05yFkQUOKIQswaz2fpPb/cEypWoSCeQV8T0w0e3YKLi4RaWWvKS1MFJDHn7xMYaTk0OhALJoV5BxRD8vJeRi5jYf3DjEgt9+xB742HrbVRDlJuTp4=\"")
                .example("player's skin")
                .example("alex")
                .example("steve")
                .parser(new Registration.SimpleParser<Skin>() {
            @Override
            public Skin parse(String s, ParseContext parseContext) {
                if (s.equalsIgnoreCase("STEVE")) {
                    return Skin.STEVE;
                } else if (s.equalsIgnoreCase("ALEX")) {
                    return Skin.ALEX;
                } else {
                    return null;
                }
            }
        }).serializer(new Serializer<Skin>() {
            @Override
            public Fields serialize(Skin skin) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("value", skin.value);
                fields.putObject("signature", skin.signature);
                fields.putObject("uuid", skin.uuid.toString());
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
                    String uuid = fields.contains("uuid") ? (String) fields.getObject("uuid") : null;
                    Logging.debug(SkinMundo.class, "value: " + value + ", signature: " + signature + ", uuid: " + uuid);
                    if (uuid == null) {
                        return new Skin(value, signature);
                    } else {
                        return new Skin(value, signature, UUID.fromString(uuid));
                    }
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
        Registration.registerEffect(EffRetrieveSkin.class,
                "retrieve [(4¦slim)] skin from (0¦file|1¦url) %-string% [[with] timeout %-timespan%] into %object%",
                "retrieve skin (2¦of %-offlineplayer%|3¦from uuid %-string%) [[with] timeout %-timespan%] into %object%");
        Registration.registerEnum(SkullType.class, "skulltype", SkullType.values())
                .document("Skull Type", "1.8.6", "A type of skull.")
                .example("if skulltype of player's tool is dragon skull:"
                        , "\tbroadcast \"%player% is auctioning a dragon head!\"")
                .example("give player wither skull");
        Registration.registerExpression(ExprSkinWith.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] (with|of) value %string% signature %string%")
                .document("Skin with Value", "1.8", "An expression for a skin with the specified value and signature.");
        Registration.registerExpression(ExprSkinOf.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] of %player/itemstack/block%", "%player/itemstack/block%'s skin")
                .document("Skin of Player or Skull", "1.8", "An expression for the skin of the specified player (must be online), skull item, or placed skull block (1.8.5+ only).")
                .changer(Changer.ChangeMode.SET, Skin.class, "1.8", "Only allowed for setting the skin of a skull (item or block). "
                        + "If the item or block wasn't already a player skull, this will also make it a player skull.");
        Registration.registerExpression(ExprDisplayedSkinOfPlayer.class, Skin.class, ExpressionType.PROPERTY,
                "[(1¦default)] displayed skin of %player% [(for %-players%|excluding %-players%)]",
                "%player%'s [(1¦default)] displayed skin [(for %-players%|excluding %-players%)]")
                .document("Displayed Skin of Player", "1.8", "An expression for the skin currently being displayed as the specified player's skin. "
                        + "If target ('for') players are specified, the expression will return a skin for each target player specified. "
                        + "Excluded players are meant to be specified only when setting the expression (for example, to prevent the original specified player from seeing a change). "
                        + "If the expression is evaluated with excluded players specified, it will act the same as if no target or excluded players had been specified.")
                .changer(Changer.ChangeMode.SET, Skin.class, "1.8", "Changes the displayed skin of the specified player. The behavior of the change differs depending on what is specified in the syntax. "
                        + "If none of the extra syntax options are specified, the player's default nametag will be changed, and all players will see the new nametag (any specific skins assigned for the specified player will be removed). "
                        + "Specifying 'default' means that only the specified player's default displayed skin will be changed, meaning that only the players who do not have a specific skin assigned for the specified player will see the new nametag. "
                        + "Specifying target players means that the displayed skin will be changed for those target players, and will become their specific skin assigned for the specified player. "
                        + "Specifying excluded players means that excluded players who do not currently have a specific skin for the specified player "
                        + "will have the default displayed skin for that player set as the specific skin, and then after that the effect will be the same as changing the default displayed skin. ")
                .changer(Changer.ChangeMode.RESET, "1.8", "If target players or excluded players are specified, this will remove any specified skin of either the target players or all non-exluded players assigned for the specified player, and revert to the default skin for the specified player. "
                        + "If no target players are specified, this will be identical to doing 'set <expression> to <specified player>'s skin', with that behavior depending on whether 'default' is specified.")
                .changer(Changer.ChangeMode.DELETE, "1.8", "Same as reset.")
                .example("set player's default displayed skin to alex #All players now see the skin as alex"
                        , "set player's displayed skin to steve for {_p1} #{_p1} now sees the skin as steve"
                        , "set player's default displayed skin to {_p2}'s skin #All players except for {_p1} now see the nametag as {_p2}'s skin"
                        , "set player's displayed skin to {_p3}'s skin #All players (including {_p1}) now see the skin as {_p3}'s skin")
                .example("set player's default displayed skin to steve #All players now see the skin as steve"
                        , "set player's displayed skin excluding {_p1} to alex #All players except for {_p1} now see the skin as alex"
                        , "reset player's default displayed skin #All players except for {_p1} now see the skin as the player's actual skin"
                        , "set player's default displayed skin to {_p3}'s skin #All players except for {_p1} now see the skin as {_p3}'s skin"
                        , "reset player's displayed skin for {_p1} #{_p1} now sees the skin as {_p3}'s skin"
                        , "set player's displayed skin to {_p4}'s skin for {_p1} #{_p1} now sees the skin as {_p4}'s skin"
                        , "reset player's displayed skin #All players (including {_p1}) now see the skin as the player's actual skin");
        Registration.registerExpression(ExprSkullFromSkin.class, ItemStack.class, ExpressionType.PROPERTY, "skull from %skin% [with owner %-string%]")
                .document("Skull from Skin", "1.8", "An expression for a skull bearing the specified skin, optionally with the specified owner. "
                        + "If you do not specify an owner, the owner will appear to be \"MundoSK-Name\". This really only matters if anybody is going to actually have the skull in their inventory "
                        + "(i.e. if the skull is only going to be used as a block, the owner isn't important, though it may be useful as an identifier).");
        Registration.registerExpression(ExprRetrievedSkin.class, Skin.class, ExpressionType.PROPERTY,
                "retrieved [(4¦slim)] skin from (0¦file|1¦url) %string% [[with] timeout %-timespan%]",
                "retrieved skin (2¦of %-offlineplayer%|3¦from uuid %-string%) [[with] timeout %-timespan%]")
                .document("Retrieved Skin", "1.8",
                        "An expression for a skin retrieved using the Mineskin API or the Mojang API:"
                        , "A skin recreated from the specified image file (Mineskin),"
                        , "A skin recreated from the specified URL of an image (Mineskin), or"
                        , "The skin of the specified offline player retrieved from Mojang");
        Registration.registerPropertyExpression(ExprOwnerOfSkull.class, String.class, "itemstack/block", "owner of skull %", "skull %'s owner")
                .document("Owner of Skull", "1.8.5", "An expression for the owner of the specified skull, as an item or placed. "
                        + "The owner only means the name that is shown when held, like \"Tlatoani's Head\", and doesn't affect the actual skin that the skull has.")
                .changer(Changer.ChangeMode.SET, String.class, "1.8.6", "If the item or block isn't already a player skull, this will make it a player skull before setting its skin.");
        Registration.registerPropertyExpression(ExprTypeOfSkull.class, SkullType.class, "itemstack/block", "skulltype", "skull type")
                .document("Type of Skull", "1.8.6", "An expression for the type of skull that is the specified skull, as an item or placed.")
                .changer(Changer.ChangeMode.SET, SkullType.class, "1.8.6", "Makes the specified item or block a skull of the specified type, "
                        + "even if it wasn't previously a skull at all.");
        Registration.registerPropertyExpression(ExprSkullOfType.class, ItemStack.class, "skulltype", "% skull", "a % skull")
                .document("Skull of Type", "1.8.6", "An expression for a skull of the given skulltype.");
        Registration.registerExpression(ExprNameTagOfPlayer.class, String.class, ExpressionType.PROPERTY,
                "[mundo[sk]] %player%'s [(1¦default)] name[]tag [for %-players%]",
                "[mundo[sk]] [(1¦default)] name[]tag of %player% [for %-players%]")
                .document("Nametag of Player", "1.8.4", "An expression for the nametag (the name that appears above a player's head) of the specified player. "
                        + "If target ('for') players are specified, the expression will return a nametag for each target player specified. ")
                .changer(Changer.ChangeMode.SET, String.class, "1.8.4", "Changes the nametag of the specified player. The behavior of the change differs depending on what is specified in the syntax. "
                        + "If none of the extra syntax options are specified, the player's default nametag will be changed, and all players will see the new nametag. "
                        + "Specifying 'default' means that only the specified player's default nametag will be changed, meaning only the players who do not have a specific nametag assigned for the specified player will see the new nametag. "
                        + "Specifying target players means that the nametag will be changed for those target players, and will become their specific nametag assigned for the specified player.")
                .changer(Changer.ChangeMode.RESET, "1.8.4", "If target players are specified, this will remove any specified nametag assigned for the specified player, and revert to the default nametag for the specified player. "
                        + "If no target players are specified, this will be identical to doing 'set <expression> to <specified player>'s name', with that behavior depending on whether 'default' is specified.")
                .changer(Changer.ChangeMode.DELETE, "1.8.4", "Same as reset.")
                .example("set player's default nametag to \"bob\" #All players now see the nametag as bob"
                        , "set player's nametag to \"potter\" for {_p1} #{_p1} now sees the nametag as potter"
                        , "set player's default nametag to \"weird\" #All players except for {_p1} now see the nametag as weird"
                        , "set player's nametag to \"nonweird\" #All players (including {_p1}) now see the nametag as nonweird")
                .example("set player's default nametag to \"diamond\" #All players now see the nametag as diamond"
                        , "set player's nametag to \"emerald\" for {_p1} #{_p1} now sees the nametag as emerald"
                        , "reset player's default nametag #All players except for {_p1} now see the nametag as the player's actual name"
                        , "set player's default nametag to \"gold\" #All players except for {_p1} now see the nametag as gold"
                        , "reset player's nametag for {_p1} #{_p1} now sees the nametag as gold"
                        , "set player's default nametag to \"iron\" for {_p1} #{_p1} now sees the nametag as iron"
                        , "reset player's nametag #All players (including {_p1}) now see the nametag as the player's actual name");
        Registration.registerExpression(ExprTabName.class, String.class, ExpressionType.PROPERTY, "%player%'s [mundo[sk]] tab[list] name", "[mundo[sk]] tab[list] name of %player%");
                //.document("Tablist Name of Player", "1.8", "An expression for the tablist name of the specified player.");
    }
}
