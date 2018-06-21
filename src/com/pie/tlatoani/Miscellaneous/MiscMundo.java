package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.SerializedVariable;
import ch.njol.yggdrasil.Fields;
import com.pie.tlatoani.Miscellaneous.ArmorStand.ArmorStandEquipmentSlot;
import com.pie.tlatoani.Miscellaneous.ArmorStand.EvtArmorStandPlace;
import com.pie.tlatoani.Miscellaneous.Hanging.EvtUnhang;
import com.pie.tlatoani.Miscellaneous.Hanging.ExprHangedEntity;
import com.pie.tlatoani.Miscellaneous.JSON.EffPutJsonInListVariable;
import com.pie.tlatoani.Miscellaneous.JSON.ExprListVariableAsJson;
import com.pie.tlatoani.Miscellaneous.JSON.ExprStringAsJson;
import com.pie.tlatoani.Miscellaneous.Matcher.ScopeMatcher;
import com.pie.tlatoani.Miscellaneous.Matcher.ScopeMatches;
import com.pie.tlatoani.Miscellaneous.MiscBukkit.*;
import com.pie.tlatoani.Miscellaneous.NoteBlock.EffPlayNoteBlock;
import com.pie.tlatoani.Miscellaneous.NoteBlock.ExprNoteOfBlock;
import com.pie.tlatoani.Miscellaneous.Random.ExprNewRandom;
import com.pie.tlatoani.Miscellaneous.Random.ExprRandomValue;
import com.pie.tlatoani.Miscellaneous.ServerListPing.ExprAmountOfPlayers;
import com.pie.tlatoani.Miscellaneous.ServerListPing.ExprIP;
import com.pie.tlatoani.Miscellaneous.ServerListPing.ExprMotd;
import com.pie.tlatoani.Miscellaneous.TabCompletion.ExprCompletions;
import com.pie.tlatoani.Miscellaneous.TabCompletion.ExprCompletionsOld;
import com.pie.tlatoani.Miscellaneous.TabCompletion.ExprLastToken;
import com.pie.tlatoani.Miscellaneous.TabCompletion.ExprLastTokenOld;
import com.pie.tlatoani.Miscellaneous.Thread.EffAsyncSetVar;
import com.pie.tlatoani.Miscellaneous.Thread.EffWaitAsync;
import com.pie.tlatoani.Miscellaneous.Thread.ScopeAsync;
import com.pie.tlatoani.Miscellaneous.Thread.ScopeSync;
import com.pie.tlatoani.Miscellaneous.Tree.ExprBranch;
import com.pie.tlatoani.Miscellaneous.Tree.ExprTreeOfListVariable;
import com.pie.tlatoani.Core.Registration.EnumClassInfo;
import com.pie.tlatoani.Core.Registration.Registration;
import com.pie.tlatoani.Util.Skript.SlotImpl;
import com.pie.tlatoani.Core.Static.MundoUtil;
import com.pie.tlatoani.Core.Static.Reflection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class MiscMundo {
    
    public static void load() {
        //Allow MundoSK 'conditions' to work in absence of SkQuery, which provides a condition like the below
        if (!MundoUtil.serverHasPlugin("SkQuery")) {
            Registration.registerCondition(CondBoolean.class, "[(1¦not)] %boolean%");
        }

        Registration.registerEffect(EffWait.class, "[(2¦async)] wait (0¦until|1¦while) %boolean% [for %-timespan%] [by %-timespan%]")
                .document("Wait While or Until", "1.8", "Delays the following code either while or until the specified boolean expression is true. "
                        + "SkQuery's 'check %predicate%' expression can be used to use a condition as the boolean expression. "
                        + "Optionally, you can specify a timespan to be the maximum delay. By default, there is no maximum delay. "
                        + "Optionally also, you can specify a timespan to be the interval to wait between checking the boolean expression. This is 1 tick by default.");
        Registration.registerExpression(ExprReturnTypeOfFunction.class,ClassInfo.class,ExpressionType.PROPERTY,"return type of function %string%")
                .document("Return Type of Function", "1.5.4", "An expression for the return type of the function with the specified name.");
        Registration.registerExpression(ExprLoadedScripts.class,String.class,ExpressionType.SIMPLE, "loaded script[ name]s")
                .document("Loaded Scripts", "1.6.9", "An expression for the names of all of the currently loaded scripts.");
        Registration.registerExpression(ExprAllTypes.class, ClassInfo.class, ExpressionType.SIMPLE, "all types")
                .document("All Types", "1.8", "An expression for a list of all of the registered types.");
        Registration.registerExpression(ExprThatAre.class, Object.class, ExpressionType.COMBINED, "%objects% that are[(1¦n't|1¦ not)] %object%")
                .document("Elements that Are", "1.8", "An expression for a list of the elements in the specified list "
                        + "that are either equal or not equal to the latter specified object.");
        Registration.registerExpression(ExprNumber.class, Number.class, ExpressionType.PROPERTY, "%*number%[ ](0¦b|1¦d|2¦f|3¦s|4¦l)")
                .document("Number as Java Type", "1.8", "An expression for getting the specified number as the specified Java primitive type:"
                        , "b = byte"
                        , "d = double"
                        , "f = float"
                        , "s = short"
                        , "l = long"
                        , "This is useful for example when modifying certain fields of packets, as they frequently require certain Java number types "
                        + "and will cause errors when the wrong type is used.");
        Registration.registerExpression(ExprLoopWhile.class,Object.class,ExpressionType.PROPERTY,"%objects% (0¦while|1¦until|2¦if|3¦unless) %boolean%")
                .document("Loop While", "1.6.4", "An expression used in loops in order to loop through a list of objects either while or until a certain boolean expression is true. "
                        + "SkQuery's 'check %predicate%' expression can be used to use a condition as the boolean expression. "
                        + "If 'while' or 'until' are used, then the expression will cause the loop to loop infinitely, repeating the objects once the end is reached, "
                        + "until the boolean expression changes. However, if 'if' or 'unless' are used, then the loop will terminate once all objects are looped through.");
        Registration.registerExpression(ExprIndexesOfListVariable.class, String.class, ExpressionType.PROPERTY, "[all [of]] [the] indexes (of|in) [value] %objects%")
                .document("Indexes of List Variable", "1.7", "An expression for all of the indexes used in the specified list variable. "
                        + "This includes indexes that refer to sub-variables, ex. if {_ex::pie::temp} is set, then \"pie\" will be included "
                        + "if {_ex::*} is specified as in this expression.");
        Registration.registerExpression(ExprForObjects.class, Object.class, ExpressionType.COMBINED, "%objects% for %object% in %objects%")
                .document("For Elements", "1.8", "An expression that, for each element of the last specified list, stores that element in the specified variable ('%object%'), "
                        + "and calculates a value (or multiple) by evaluating the first specified expression, then returns all of the calculated values. "
                        + "For example, evaluating '({_x} + 1) for {_x} in 1, 2, 3' would return '2, 3, 4'.");
        Registration.registerScope(ScopeWhen.class, "when %boolean% [by %-timespan%]")
                .document("When Boolean is True", "1.8", "A scope that waits until the specified boolean expression is true, then executes the block of code under it. "
                        + "The lines of code that come after the scope execute immediately, rather than waiting for the boolean expression to be true. "
                        + "SkQuery's 'check %predicate%' expression can be used to use a condition as the boolean expression. "
                        + "Optionally, you can specify a timespan to be the interval to wait between checking the boolean expression. This is 1 tick by default.");

        loadArmorStand();
        loadHanging();
        loadJSON();
        loadMatcher();
        loadMiscBukkit();
        loadNoteBlock();
        loadRandom();
        loadServerListPing();
        loadTabCompletion();
        loadThread();
        loadTree();
    }
    
    private static void loadArmorStand() {
        Registration.registerEvent("Armor Stand Interact Event", SimpleEvent.class, PlayerArmorStandManipulateEvent.class, "armor stand (manipulate|interact)")
                .document("Armor Stand Interact", "1.6.9", "Called when a player right clicks an armor stand to put something on it / take something off of it. "
                        + "The event is called before the interaction fully happens, meaning, for example, player's tool is what was in the player's hand before interacting, "
                        + "and (if it isn't air) represents what the player is putting on the armor stand. "
                        + "Note: If you are using Umbaska, make sure to use manipulate instead of interact, as interact conflicts with an Umbaska event.")
                .eventValue(Player.class, "1.6.9", "The player.")
                .eventValue(Entity.class, "1.6.9", "The armor stand.")
                .eventValue(ItemStack.class, "1.6.9", "The item being taken off.")
                .eventValue(SlotImpl.getSkriptSlotClass(), "1.6.9", "The equipment slot, use this to set the item in that particular event.");
        Registration.registerEventValue(PlayerArmorStandManipulateEvent.class, ItemStack.class, PlayerArmorStandManipulateEvent::getArmorStandItem);
        SlotImpl.registerEventValue(PlayerArmorStandManipulateEvent.class, e ->
            new ArmorStandEquipmentSlot(e.getRightClicked(), e.getSlot()));
        Registration.registerEvent("Armor Stand Place Event", EvtArmorStandPlace.class, EntitySpawnEvent.class, "armor stand place")
                .document("Armor Stand Place", "1.6.9", "Called when an armor stand is placed")
                .eventValue(Entity.class, "1.6.9", "The armor stand that was placed");
    }

    private static void loadHanging() {
        Registration.registerEnum(HangingBreakEvent.RemoveCause.class, "hangingremovecause", HangingBreakEvent.RemoveCause.values())
                .document("HangingRemoveCause", "1.8", "A cause for a hanged entity to have been unhung.");
        Registration.registerEvent("Hang Event", SimpleEvent.class, HangingPlaceEvent.class, "hang")
                .document("Hang", "1.6.4", "Called when an entity is hung. Can be cancelled. Also see the Hanged Entity expression.")
                .eventValue(Entity.class, "1.6.4", "The entity that hung the hanged entity.")
                .eventValue(Player.class, "1.6.4", "The player that hung the hanged entity (same as event-entity).")
                .eventValue(Block.class, "1.6.4", "The block on which the hanged entity was hung.");
        Registration.registerEventValue(HangingPlaceEvent.class, Block.class, HangingPlaceEvent::getBlock);
        Registration.registerEvent("Unhang Event", EvtUnhang.class, HangingBreakEvent.class, "unhang [due to %-hangingremovecauses%]")
                .document("Unhang", "1.8", "Called when an entity is unhung. Can be cancelled. "
                        + "Optionally, you can specify hanging remove causes which make the trigger only be called if the unhanging is due to those reasons. "
                        + "Also see the Hanged Entity expression.")
                .eventValue(Entity.class, "1.8", "The entity that unhung the hanged entity.");
        Registration.registerEventValue(HangingBreakByEntityEvent.class, Entity.class, HangingBreakByEntityEvent::getRemover);
        Registration.registerEventValue(HangingBreakEvent.class, HangingBreakEvent.RemoveCause.class, HangingBreakEvent::getCause);
        Registration.registerExpression(ExprHangedEntity.class,Entity.class, ExpressionType.SIMPLE,"hanged entity")
                .document("Hanged Entity", "1.6.5", "An expression, used in the Hang and Unhang events, for the entity which was hung/unhung.");
    }

    public static Object serializeJSONElement(Object object) {
        if (object instanceof JSONArray) {
            JSONArray result = new JSONArray();
            for (Object elem : (JSONArray) object) {
                Object serializedElem = serializeJSONElement(elem);
                if (serializedElem != null) {
                    result.add(serializedElem);
                }
            }
            return result;
        }
        SerializedVariable.Value value = Classes.serialize(object);
        if (value == null) {
            return null;
        }
        JSONObject valueJSON = new JSONObject();
        valueJSON.put("type", value.type);
        valueJSON.put("data", new String(value.data));
        return valueJSON;
    }

    public static Object deserializeJSONElement(Object object) {
        if (object instanceof JSONArray) {
            JSONArray result = new JSONArray();
            for (Object serializedElem : (JSONArray) object) {
                Object deserializedElem = deserializeJSONElement(serializedElem);
                result.add(deserializedElem);
            }
            return result;
        }
        JSONObject jsonObject = (JSONObject) object;
        String type = (String) jsonObject.get("type");
        String dataString = (String) jsonObject.get("data");
        if (dataString == null) {
            dataString = (String) jsonObject.get("Data");
        }
        byte[] data = dataString.getBytes();
        return Classes.deserialize(type, data);
    }

    private static void loadJSON() {
        Registration.registerType(JSONObject.class, "jsonobject")
                .document("JSONObject", "1.6.4", "A JSONObject, a type of data structure used for storing information in the form of "
                        + "keys/indexes and values (like a list variable). Useful in Skript for transmitting complex information that isn't represented by a type "
                        + "in cases where list variables can't be used (ex. returning values from expressions/functions (ex. the JSON Field of Packet expression)).")
                .parser(new Registration.SimpleParser<JSONObject>() {
            @Override
            public JSONObject parse(String s, ParseContext parseContext) {
                try {
                    return (JSONObject) (new JSONParser()).parse(s);
                } catch (ParseException | ClassCastException e) {
                    return null;
                }
            }
        }).serializer(new Serializer<JSONObject>() {
            @Override
            public Fields serialize(JSONObject jsonObject) throws NotSerializableException {
                JSONObject toBecomeString = new JSONObject();
                jsonObject.forEach((o, o2) -> {
                    Object serializedValue = serializeJSONElement(o2);
                    toBecomeString.put(o, serializedValue);
                });
                Fields fields = new Fields();
                fields.putObject("value", toBecomeString.toJSONString());
                return fields;
            }

            @Override
            public void deserialize(JSONObject jsonObject, Fields fields) throws StreamCorruptedException, NotSerializableException {
                try {
                    JSONObject fromString = (JSONObject) (new JSONParser()).parse((String) fields.getObject("value"));
                    fromString.forEach((o, o2) -> {
                        jsonObject.put(o, deserializeJSONElement(o2));
                    });
                } catch (ParseException | ClassCastException | NullPointerException e) {
                    throw new StreamCorruptedException();
                }
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return true;
            }
        });
        Registration.registerEffect(EffPutJsonInListVariable.class, "put json %jsonobject% in listvar %objects%", "put jsons %jsonobjects% in listvar %objects%")
                .document("Put JSON in List Variable", "1.6.4", "Puts all of the information stored inside the specified jsonobject into the specified list variable. "
                        + "This is needed as storing json data in list variables is currently the only way to manipulate information in MundoSK's jsonobjects other than raw string manipulation.");
        Registration.registerExpression(ExprListVariableAsJson.class, JSONObject.class, ExpressionType.PROPERTY, "json (of|from) (listvar|list variable) %objects%", "jsons (of|from) (listvar|list variable) %objects%")
                .document("JSON from List Variable", "1.6.4", "An expression for a jsonobject constructed from the information stored inside the specified list variable. ");
        Registration.registerExpression(ExprStringAsJson.class, JSONObject.class, ExpressionType.PROPERTY, "json of string %string%");
    }
    
    private static void loadMatcher() {
        Registration.registerScope(ScopeMatcher.class, "(switch|match) %object%")
                .document("Switch", "1.7.2", "The switch scope allows you to specify an object, then below the switch scope, insert case scopes, "
                        + "and when run, the switch scope will go through each case scope and check which one is equal to the switch scope's object, "
                        + "then run that case scope's code block.")
                .example("match 3:"
                        , "\tcase 1:"
                        , "\t\tbroadcast \"ONE\""
                        , "\tcase 2:"
                        , "\t\tbroadcast \"TWO\""
                        , "\tcase 3:"
                        , "\t\tbroadcast \"THREE\""
                        , "\ttrue:"
                        , "\t\tbroadcast \"SOMETHING ELSE\""
                        , "#\"THREE\" will be broadcasted");
        Registration.registerScope(ScopeMatches.class, "(case|matches) %object%")
                .document("Case", "1.7.2", "The case scope is written underneath a switch scope, specifying an object which, if equal to the switch scope's specified object, "
                        + "will cause the case scope's underlying code block to be run.");
    }
    
    private static void loadMiscBukkit() {
        Registration.registerEnum(Difficulty.class, "difficulty", Difficulty.values())
                .document("Difficulty", "1.4.4 or earlier", "The difficulty of a world.");
        Registration.registerEnum(PlayerLoginEvent.Result.class, "playerloginresult", PlayerLoginEvent.Result.values())
                .document("Player Login Result", "1.7", "A result of a player login attempt.");
        Registration.registerPropertyExpression(ExprWorldByName.class, World.class, "string", "world %")
                .document("World from Name", "1.1 or earlier", "An expression for the world with the specified name.");
        Registration.registerExpression(ExprHighestSolidBlock.class, Block.class, ExpressionType.PROPERTY,"highest [(solid|non-air)] block at %location%")
                .document("Highest Solid Block", "1.4.4 or earlier", "An expression for the highest block that isn't air at a specific location. Useful for setting spawns.");
        Registration.registerPropertyExpression(ExprDifficulty.class, Difficulty.class, "world", "difficulty")
                .document("Difficulty of World", "1.4.4 or earlier", "An expression for the difficulty of the specified world. See the Difficulty type for values.");
        Registration.registerExpression(ExprGameRule.class, String.class, ExpressionType.PROPERTY,"value of [game]rule %string% in %world%")
                .document("Value of Gamerule", "1.4.4 or earlier", "An expression for the value of the specified gamerule in the specified world.");
        Registration.registerPropertyExpression(ExprRemainingAir.class, Timespan.class, "livingentity", "breath", "max breath")
                .document("Breath of Living Entity", "1.6", "An expression for the amount of breath (the bubbles that appear on the hotbar when you go underwater) "
                        + " of the specified underwater living entity, or its maximum possible breath.")
                .example("if player's breath is less than 3 seconds:"
                        , "\tmessage \"Running low on air!\"");
        Registration.registerExpression(ExprLoginResult.class, PlayerLoginEvent.Result.class, ExpressionType.SIMPLE, "(login|connect[ion]) result")
                .document("Login Result", "1.7", "An expression, for use in the 'on connect' event, for the result of the connection attempt. "
                        + "See the PlayerLoginResult type for possible values.");
        Registration.registerExpression(ExprServerIP.class, String.class, ExpressionType.SIMPLE, "[mundo[sk]] [the] ip of server", "[mundo[sk]] [the] server's ip")
                .document("IP of Server", "1.8", "An expression for the IP of your Minecraft server.");
        Registration.registerExpression(ExprServerPort.class, Number.class, ExpressionType.SIMPLE, "[mundo[sk]] [the] port of server", "[mundo[sk]] [the] server's port")
                .document("Port of Server", "1.8", "An expression for the port of your Minecraft server.");
        Registration.registerExpressionCondition(CondCollidable.class, ExpressionType.PROPERTY, "%livingentities% (0¦is|0¦are|1¦isn't|1¦is not|1¦aren't|1¦are not) collidable")
                .document("Living Entity is Collidable", "1.8", "Checks whether the specified living entity is collidable. "
                        + "Note that it is possible that an entity is non-collidable due to circumstance (ex. the entity is dead) "
                        + "and this condition/expression still be true as the entity would normally be collidable. "
                        + "In addition, if an entity that is collidable collides with another entity that is non-collidable "
                        + "due to this condition/expression being false, they will still collide.");
        Registration.registerExpression(ExprTreeAtLoc.class, Block.class, ExpressionType.PROPERTY, "tree at %location%")
                .document("Tree at Location", "1.8", "An expression for the blocks that make up a tree which has a block at the specified location. "
                        + "Note that if two trees are connected through leaves or wood, both of their blocks (and the blocks of any further connected trees) "
                        + "will be returned.");
        Registration.registerExpression(ExprRespawnLocation.class, Location.class, ExpressionType.SIMPLE, "respawn location")
                .document("Respawn Location", "1.8", "An expression, used in the 'on respawn' event, for the location at which a player respawned.");
        Registration.registerExpression(ExprDestination.class, Location.class, ExpressionType.SIMPLE, "destination")
                .document("Destination", "1.8", "An expression, used in the 'on teleport' event, for the destination of the teleporting entity.");
        Registration.registerExpression(ExprNewPortal.class, Location.class, ExpressionType.PROPERTY, "new nether portal within [[a] radius of] %number% (block|meter)s of %location%")
                .document("New Nether Portal", "1.8", "An expression, used in the 'on [player] portal' event, that attempts to create a new nether portal within the specified radius of the specified location, "
                        + "and returns the location of the created portal if successful, and is not set otherwise.");
    }

    private static void loadNoteBlock() {
        Map<String, Note> noteMap = new HashMap<>();
        for (int octave : new int[]{0, 1})
            for (Note.Tone tone : Note.Tone.values())
                for (int deviation : new int[]{-1, 0, 1}) {
                    if (deviation == 1 && (tone == Note.Tone.B || tone == Note.Tone.E)) {
                        continue;
                    }
                    if (deviation == -1 && (tone == Note.Tone.C || tone == Note.Tone.F)) {
                        continue;
                    }
                    Note note = Note.natural(octave, tone);
                    if (deviation == 1) {
                        note = note.sharped();
                    } else if (deviation == -1) {
                        note = note.flattened();
                    }
                    String noteName = tone.name() + (deviation == 1 ? "+" : deviation == -1 ? "-" : "") + octave;
                    noteMap.put("N" + noteName, note);
                    if (octave == 0) {
                        noteMap.put("N" + noteName.substring(0, noteName.length() - 1), note);
                    }
                }
        Note fSharp2 = Note.sharp(2, Note.Tone.F);
        noteMap.put("NF+2", fSharp2);
        noteMap.put("NG-2", fSharp2);
        EnumClassInfo<Note> noteEnumClassInfo = Registration.registerEnum(Note.class, "note", noteMap);
        if (!MundoUtil.serverHasPlugin("RandomSK")) {
            noteMap.forEach((noteName, note) -> noteEnumClassInfo.pair(noteName.substring(1), note));
        }
        Registration.registerEnum(Instrument.class, "instrument", Instrument.values())
                .document("Instrument", "1.6", "An instrument to which a note can be played using a noteblock.");
        Registration.registerEffect(EffPlayNoteBlock.class, "play [[%-note% with] %-instrument% on] noteblock %block%")
                .document("Play Note on Noteblock", "1.6", "Plays the specified noteblock, optionally with a specified instrument and a specified note.");
        Registration.registerEvent("Note Play", SimpleEvent.class, NotePlayEvent.class, "note play")
                .document("Note Play", "1.6", "Called when a noteblock is played.")
                .eventValue(Note.class, "1.6", "The note that was played.")
                .eventValue(Instrument.class, "1.6", "The instrument using which the note was played.")
                .eventValue(Block.class, "1.6", "The noteblock that was played.");
        Registration.registerEventValue(NotePlayEvent.class, Note.class, NotePlayEvent::getNote);
        Registration.registerEventValue(NotePlayEvent.class, Instrument.class, NotePlayEvent::getInstrument);
        Registration.registerEventValue(NotePlayEvent.class, Block.class, NotePlayEvent::getBlock);
        Registration.registerPropertyExpression(ExprNoteOfBlock.class, Note.class, "block", "note")
                .document("Note of Noteblock", "1.6", "The current note of the specified noteblock.");
    }
    
    private static void loadRandom() {
        Registration.registerType(Random.class, "random")
                .document("Random", "1.7", "An object that can be used to generate random or pseudo-random numbers.")
                .defaultExpression((new ExprNewRandom()).setDefault());
        Registration.registerExpression(ExprNewRandom.class, Random.class, ExpressionType.PROPERTY, "new random [from seed %number%]")
                .document("New Random", "1.7", "An expression for a new random from the specified seed, or an arbitrary seed.");
        Registration.registerExpression(ExprRandomValue.class, Object.class, ExpressionType.PROPERTY, "random (0¦int|1¦long|2¦float|3¦double|4¦gaussian|5¦int less than %-number%|6¦boolean) [from [random] %random%]")
                .document("Random Value from Random", "1.7", "A random value of the specified type, either from a specified random, or from an arbitrary one. "
                        + "In the case of the 'int less than' syntax, the random value will be an int greater than or equal to 0 but less than the specified number.");
    }
    
    private static void loadServerListPing() {
        Registration.registerEvent("Server List Ping", SimpleEvent.class, ServerListPingEvent.class, "[[(server|player)] list] ping")
                .document("Server List Ping", "1.8", "Called when a Minecraft client pings the server to show information in the server list.");
        Registration.registerExpression(ExprAmountOfPlayers.class, Number.class, ExpressionType.SIMPLE, "(shown|sent) (0¦amount of|1¦max [amount of]) players")
                .document("Shown Amount of Players", "1.8", "An expression, used in the Server List Ping event, for the amount of players currently online "
                        + "or max amount of players allowed that was shown by your server to the pinging client.");
        Registration.registerExpression(ExprMotd.class, String.class, ExpressionType.SIMPLE, "(shown|sent) motd")
                .document("Shown MOTD", "1.8", "An expression, used in the Server List Ping event, for the MOTD shown by your server to the pinging client.");
        Registration.registerExpression(ExprIP.class, String.class, ExpressionType.SIMPLE, "pinger's ip", "ip of pinger")
                .document("IP of Pinger", "1.8", "An expression, used in the Server List Ping event, for the IP of the pinging client.");
    }
    
    private static void loadTabCompletion() {
        Registration.registerEvent("Chat Tab Complete Event", SimpleEvent.class, PlayerChatTabCompleteEvent.class, "chat tab complete")
                .document("Chat Tab Complete", "1.8", "Called when a player uses tab to auto complete a message (not a command). "
                        + "Also see the Completions and Last Token expressions.")
                .eventValue(String.class, "1.8", "The full message so far typed by the player.")
                .eventValue(Player.class, "1.8", "The player typing the message.");
        Registration.registerEventValue(PlayerChatTabCompleteEvent.class, String.class, PlayerChatTabCompleteEvent::getChatMessage);
        if (Reflection.classExists("org.bukkit.event.server.TabCompleteEvent")) {
            Registration.registerEvent("Tab Complete Event", SimpleEvent.class, TabCompleteEvent.class, "tab complete")
                    .document("Tab Complete", "1.8", "Called when a player uses tab to auto complete a message or a command. "
                            + "Only available in recent Bukkit versions. Also see the Completions and Last Token expressions.")
                    .eventValue(CommandSender.class, "1.8.4", "The command sender typing the message or command.")
                    .eventValue(Player.class, "1.8.4", "The player typing the message or command, if it is a player.")
                    .eventValue(String.class, "1.8", "The full message so far typed by the player.");
            Registration.registerEventValue(TabCompleteEvent.class, CommandSender.class, TabCompleteEvent::getSender);
            Registration.registerEventValue(TabCompleteEvent.class, String.class, TabCompleteEvent::getBuffer);
            Registration.registerExpression(ExprCompletions.class, String.class, ExpressionType.SIMPLE,"completions")
                    .document("Completions", "1.6.8", "An editable expression, used in the Tab Complete and Chat Tab Complete events, "
                            + "for a list of all completions available for this tab complete.");
            Registration.registerExpression(ExprLastToken.class, String.class, ExpressionType.SIMPLE, "last token")
                    .document("Last Token", "1.6.8", "An expression, used in the Tab Complete and Chat Tab Complete events, "
                            + "for the last token typed by the player before tab completing.");
        } else {
            Registration.registerExpression(ExprCompletionsOld.class,String.class,ExpressionType.SIMPLE,"completions")
                    .document("Completions", "1.6.8", "An editable expression, used in the Chat Tab Complete event, "
                            + "for a list of all completions available for this tab complete.");
            Registration.registerExpression(ExprLastTokenOld.class, String.class, ExpressionType.SIMPLE, "last token")
                    .document("Last Token", "1.6.8", "An expression, used in the Chat Tab Complete event, "
                            + "for the last token typed by the player before tab completing.");
        }
    }
    
    private static void loadThread() {
        Registration.registerEffect(EffWaitAsync.class, "async wait %timespan%")
                .document("Async Wait", "1.8", "The asynchronous equivalent of Skript's built-in 'wait %timespan%' effect, "
                        + "as just using that effect in asynchronous code will make the rest of the code run synchronously.");
        Registration.registerEffect(EffAsyncSetVar.class, "async set %objects% to %objects%")
                .document("Async Set", "1.8", "Evaluates the second specified expression asynchronously, "
                        + "then sets the specified variable (the first specified expression) to that value and continues the rest of the code synchronously.");
        Registration.registerScope(ScopeAsync.class, "async [in %-timespan%]")
                .document("Async in Timespan", "1.8", "A scope used to run a certain block of code asynchronously. "
                        + "This means that the code will be run on a separate thread from the main server thread, "
                        + "allowing you to perform tasks such as making web requests without lagging the server. "
                        + "Optionally, you can specify a timespan to wait before running the block of code. "
                        + "The lines of code following the scope will run as normal without waiting for any of the block of code to start/finish. "
                        + "It is also possible to write this scope as a regular line of code, in which case instead of a block of code below a scope, "
                        + "all following lines of code will be forced to run asynchronously, as well as wait a certain timespan if it is specified.")
                .example("broadcast \"Retrieving text!\""
                        , "async:"
                        , "\tset {_var} to text from url \"mundosk.github.io\""
                        , "wait until {_var} is set"
                        , "broadcast \"Text retrieved: %{_var}%\"")
                .example("broadcast \"Retrieving text!\""
                        , "async"
                        , "set {_var} to text from url \"mundosk.github.io\""
                        , "sync"
                        , "broadcast \"Text retrieved: %{_var}%\"");
        Registration.registerScope(ScopeSync.class, "(sync|in %-timespan%)")
                .document("Sync in Timespan", "1.8", "A scope used to run a certain block of code synchronously. "
                        + "This means that the code will be run on the main server thread, "
                        + "allowing you to perform tasks relating to Bukkit, such as changing a block. "
                        + "This is useful when code running asynchronously needs to perform such tasks. "
                        + "Optionally, you can specify a timespan to wait before running the block of code. "
                        + "The lines of code following the scope will run as normal without waiting for any of the block of code to start/finish. "
                        + "It is also possible to write this scope as a regular line of code, in which case instead of a block of code below a scope, "
                        + "all following lines of code will be forced to run synchronously, as well as wait a certain timespan if it is specified "
                        + "(this behavior is mostly equivalent to Skript's 'wait %timespan%' effect).");
    }

    private static void loadTree() {
        Registration.registerExpression(ExprTreeOfListVariable.class, Object.class, ExpressionType.PROPERTY, "tree of %objects%")
                .document("Tree Loop", "1.6.4", "An expression meant to only be used in loops, that allows you to loop through all of the indexes and values "
                        + "of the specified list variable, including the sub-indexes and sub-values. For example, if you do 'set {_temp::pie::1} to \"ex\" in Skript, "
                        + "looping through {_temp::*} will ignore that value. However, the tree loop allows you to loop through all possible values contained inside the specified list variable. "
                        + "Make sure to also see the Branch expression. Note: before MundoSK 1.8, modifying the list variable while looping through its tree sometimes caused errors. "
                        + "As of MundoSK 1.8, this will not cause errors. Instead, these modifications will be ignored during the tree loop.");
        Registration.registerExpression(ExprBranch.class, String.class, ExpressionType.PROPERTY, "branch")
                .document("Branch", "1.6.4", "An expression, used in a tree loop, for the full index, or branch, of the current value in the tree loop's list variable.");
    }

}
