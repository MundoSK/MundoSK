package com.pie.tlatoani;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Converter;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.util.Slot;

import ch.njol.skript.variables.SerializedVariable;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Checker;
import ch.njol.util.Kleenean;
import ch.njol.util.Pair;
import ch.njol.yggdrasil.Fields;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import com.pie.tlatoani.Book.*;
import com.pie.tlatoani.Chunk.*;
import com.pie.tlatoani.CodeBlock.*;
import com.pie.tlatoani.CustomEvent.*;
import com.pie.tlatoani.EnchantedBook.*;
import com.pie.tlatoani.Generator.*;
import com.pie.tlatoani.Generator.Seed.*;
import com.pie.tlatoani.Json.*;
import com.pie.tlatoani.ListUtil.*;
import com.pie.tlatoani.Miscellaneous.*;
import com.pie.tlatoani.Miscellaneous.ArmorStand.*;
import com.pie.tlatoani.Miscellaneous.Matcher.*;
import com.pie.tlatoani.Miscellaneous.MiscBukkit.*;
import com.pie.tlatoani.Miscellaneous.ServerListPing.ExprAmountOfPlayers;
import com.pie.tlatoani.Miscellaneous.ServerListPing.ExprIP;
import com.pie.tlatoani.Miscellaneous.ServerListPing.ExprMotd;
import com.pie.tlatoani.Miscellaneous.Thread.*;
import com.pie.tlatoani.NoteBlock.*;
import com.pie.tlatoani.Probability.*;
import com.pie.tlatoani.ProtocolLib.*;
import com.pie.tlatoani.Skin.*;
import com.pie.tlatoani.Socket.*;
import com.pie.tlatoani.Tablist.*;
import com.pie.tlatoani.Tablist.Array.ArrayTablist;
import com.pie.tlatoani.Tablist.Array.EffEnableArrayTablist;
import com.pie.tlatoani.Tablist.Simple.ExprIconOfTab;
import com.pie.tlatoani.TerrainControl.*;
import com.pie.tlatoani.Throwable.*;
import com.pie.tlatoani.Util.*;
import com.pie.tlatoani.WebSocket.*;
import com.pie.tlatoani.WebSocket.Events.*;
import com.pie.tlatoani.WorldBorder.*;
import com.pie.tlatoani.WorldCreator.*;
import com.pie.tlatoani.WorldManagement.*;
import com.pie.tlatoani.WorldManagement.WorldLoader.*;
import com.pie.tlatoani.Metrics.*;

import com.pie.tlatoani.ZExperimental.CustomEffect;
import com.pie.tlatoani.ZExperimental.CustomElementEvent;
//import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.WebSocket;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.*;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Mundo extends JavaPlugin {
	public static Mundo instance;
    public static String pluginFolder;
    public static List<String> debugPackages;
    //public static Boolean debugMode;
    public static Boolean implementPacketStuff;
    public static int tablistRemoveTabDelaySpawn;
    public static int tablistRemoveTabDelayRespawn;
    public static String hexDigits = "0123456789abcdef";
    public static BukkitScheduler scheduler;

    public static ArrayList<Object[]> ena = new ArrayList<>();
    public static ArrayList<String> enumNames = new ArrayList<>();
    public static ArrayList<Class<?>> enumClasses = new ArrayList<>();

    @Override
	public void onEnable() {
        pluginFolder = getDataFolder().getAbsolutePath();
        FileConfiguration config = getConfig();
        config.addDefault("debug", Arrays.asList(new String[0]));
        //config.addDefault("debug_mode", false);
        config.addDefault("enable_custom_skin_and_tablist", true);
        config.addDefault("tablist_remove_tab_delay_spawn", 5);
        config.addDefault("tablist_remove_tab_delay_respawn", 5);
        config.options().copyDefaults(true);
        debugPackages = config.getStringList("debug");
        //debugMode = config.getBoolean("debug_mode");
        implementPacketStuff = config.getBoolean("enable_custom_skin_and_tablist");
        tablistRemoveTabDelaySpawn = config.getInt("tablist_remove_tab_delay_spawn");
        tablistRemoveTabDelayRespawn = config.getInt("tablist_remove_tab_delay_respawn");
        saveConfig();
        instance = this;
        UtilWorldLoader.load();
		Skript.registerAddon(this);
        scheduler = Bukkit.getScheduler();
        info("Pie is awesome :D");
        if (getDescription().getVersion().toUpperCase().contains("BETA")) {
            info("You are currently running a BETA version of MundoSK");
            info("You should only run BETA versions of MundoSK on test servers unless Tlatoani or another reliable source has recommended otherwise");
        }
        if (!debugPackages.isEmpty()) {
            info("You have enabled debug for certain packages in MundoSK config");
            info("Debug should only be enabled when you are trying to fix a bug or assist someone else with fixing a bug in MundoSK");
            info("By having debug enabled, you will have tons of random annoying spam in your console");
            info("If you would like to disable debug, simply go to your 'plugins' folder, go to the 'MundoSK' folder, open 'config.yml', and where it says 'debug', remove all following text");
        }

        //Allow MundoSK 'conditions' to work in absence of SkQuery, which provides a condition like the below
        if (!serverHasPlugin("SkQuery")) {
            registerCondition(CondBoolean.class, "%boolean%");
        }
		//Book
        ListUtil.registerTransformer("itemstack", TransBookPages.class, "page");
		registerExpression(ExprBook.class,ItemStack.class,ExpressionType.COMBINED,"%itemstack% titled %string%, [written] by %string%, [with] pages %strings%");
		registerExpression(ExprTitleOfBook.class,String.class,ExpressionType.PROPERTY,"title of %itemstack%");
		registerExpression(ExprAuthorOfBook.class,String.class,ExpressionType.PROPERTY,"author of %itemstack%");
		//Chunk
        registerEffect(EffLoadChunk.class, "(0¦load|1¦unload) chunk %chunk%");
        registerExpression(ExprChunk.class, Chunk.class, ExpressionType.COMBINED,
                "chunk %number%, %number% [in %world%]",
                "chunks [from] %number%, %number% to %number%, %number% [in %world%]");
        registerExpression(ExprChunkBlock.class, Block.class, ExpressionType.PROPERTY,
                "block %number%, %number%, %number% (of|in) %chunk%",
                "(0¦layer %-number%|1¦top|2¦bottom|3¦sea level) (0¦south|4¦north)(0¦east|8¦west) (0¦center|16¦corner) of %chunk%");
        registerExpression(ExprChunkBlocks.class, Block.class, ExpressionType.PROPERTY,
                "[all] blocks (of|in) %chunk%",
                "blocks [from] %number%, %number%, %number% to %number%, %number%, %number% (of|in) %chunk%",
                "(0¦layer %-number%|1¦top|2¦bottom|3¦sea level) (of|in) %chunk%",
                "[from] (0¦layer %-number%|1¦top|2¦bottom|3¦sea level) to (0¦layer %-number%|4¦top|8¦bottom|12¦sea level) (of|in) %chunk%",
                "layers [from] %number% to %number% (of|in) %chunk%"
        );
        registerExpression(CondSlimey.class, Boolean.class, ExpressionType.PROPERTY, "%chunks% (is|are) slimey");
        registerExpression(CondChunkLoaded.class, Boolean.class, ExpressionType.PROPERTY, "[chunk[s]] %chunks% (is|are) loaded");
		//CodeBlock
        registerType(CodeBlock.class, "codeblock");
        registerScope(ScopeSaveCodeBlock.class, "codeblock %object% [with (1¦constant|2¦constant %-object%|3¦constants %-objects%)] [:: %-strings%] [-> %-string%]");
        registerEffect(EffRunCodeBlock.class, "((run|execute) codeblock|codeblock (run|execute)) %codeblocks% [(2¦with %-objects%|3¦with variables %-objects%|4¦in a chain|5¦here|7¦with variables %-objects% in a chain)]");
        registerExpression(ExprFunctionCodeBlock.class, CodeBlock.class, ExpressionType.PROPERTY, "[codeblock of] function %string%");
        //CustomEvent
        registerEffect(EffCallCustomEvent.class, "call custom event %string% [to] [det[ail]s %-objects%] [arg[ument]s %-objects%]");
        registerEvent("Custom Event", EvtCustomEvent.class, UtilCustomEvent.class, "ev[en]t %strings%");
        registerExpression(ExprIDOfCustomEvent.class,String.class,ExpressionType.PROPERTY,"id of custom event", "custom event's id");
        registerExpression(ExprArgsOfCustomEvent.class,Object.class,ExpressionType.PROPERTY,"args of custom event", "custom event's args");
        //EnchantedBook
		registerExpression(ExprEnchBookWithEnch.class,ItemStack.class,ExpressionType.PROPERTY,"%itemstack% containing %enchantmenttypes%");
		registerExpression(ExprEnchantLevelInEnchBook.class,Integer.class,ExpressionType.PROPERTY,"level of %enchantmenttype% within %itemstack%");
		registerExpression(ExprEnchantsInEnchBook.class,EnchantmentType.class,ExpressionType.PROPERTY,"enchants within %itemstack%");
		//Generator
        registerType(ChunkData.class, "chunkdata");
        registerType(BiomeGrid.class, "biomegrid");
        registerType(Random.class, "random").defaultExpression((new ExprNewRandom()).setDefault());
        registerEffect(EffSetRegionInChunkData.class,
                "fill region from %number%, %number%, %number% to %number%, %number%, %number% in %chunkdata% with %itemstack%",
                "fill layer %number% in %chunkdata% with %itemstack%",
                "fill layers %number% to %number% in %chunkdata% with %itemstack%");
        registerEvent("World Generator", EvtChunkGenerator.class, SkriptGeneratorEvent.class, "[custom] [(world|chunk)] generator %string%");
        EventValues.registerEventValue(SkriptGeneratorEvent.class, World.class, new Getter<World, SkriptGeneratorEvent>() {
            @Override
            public World get(SkriptGeneratorEvent skriptGeneratorEvent) {
                return skriptGeneratorEvent.world;
            }
        }, 0);
        registerEventValue(SkriptGeneratorEvent.class, World.class, e -> e.world);
        registerEventValue(SkriptGeneratorEvent.class, Random.class, e -> e.random);
        registerEventValue(SkriptGeneratorEvent.class, BiomeGrid.class, e -> e.biomeGrid);
        registerEventValue(SkriptGeneratorEvent.class, Chunk.class, e -> e.chunk);
        registerExpression(ExprCurrentChunkCoordinate.class, Number.class, ExpressionType.SIMPLE, "current x", "current z");
        registerExpression(ExprMaterialInChunkData.class, ItemStack.class, ExpressionType.PROPERTY, "material at %number%, %number%, %number% in %chunkdata%");
        registerExpression(ExprBiomeInGrid.class, Biome.class, ExpressionType.PROPERTY, "biome at %number%, %number% in grid %biomegrid%");
        registerScope(ScopeGeneration.class, "generation");
        registerScope(ScopePopulation.class, "population");
        //Random
        registerExpression(ExprNewRandom.class, Random.class, ExpressionType.PROPERTY, "new random [from seed %number%]");
        registerExpression(ExprRandomValue.class, Object.class, ExpressionType.PROPERTY, "random (0¦int|1¦long|2¦float|3¦double|4¦gaussian|5¦int less than %-number%|6¦boolean) [from [random] %random%]");
        //Json
        registerType(JSONObject.class, "jsonobject").parser(new SimpleParser<JSONObject>() {
            @Override
            public JSONObject parse(String s, ParseContext parseContext) {
                JSONObject result = null;
                try {
                    result = (JSONObject) (new JSONParser()).parse(s);
                } catch (ParseException | ClassCastException e) {
                    //If parsing to a JSONObject fails, return null
                }
                return result;
            }
        }).serializer(new Serializer<JSONObject>() {
            @Override
            public Fields serialize(JSONObject jsonObject) throws NotSerializableException {
                JSONObject toBecomeString = new JSONObject();
                jsonObject.forEach(new BiConsumer() {
                    @Override
                    public void accept(Object o, Object o2) {
                        SerializedVariable.Value value = Classes.serialize(o2);
                        if (value != null) {
                            JSONObject valueJSON = new JSONObject();
                            valueJSON.put("type", value.type);
                            valueJSON.put("Data", new String(value.data));
                            toBecomeString.put(o, valueJSON);
                        }
                    }
                });
                Fields fields = new Fields();
                fields.putObject("value", toBecomeString.toJSONString());
                return fields;
            }

            @Override
            public void deserialize(JSONObject jsonObject, Fields fields) throws StreamCorruptedException, NotSerializableException {
                try {
                    JSONObject fromString = (JSONObject) (new JSONParser()).parse((String) fields.getObject("value"));
                    fromString.forEach(new BiConsumer() {
                        @Override
                        public void accept(Object o, Object o2) {
                            JSONObject valueJSON = (JSONObject) o2;
                            Object value = Classes.deserialize((String) valueJSON.get("type"), ((String) valueJSON.get("Data")).getBytes());
                            jsonObject.put(o, value);
                        }
                    });
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
                return true;
            }
        });
        registerEffect(EffPutJsonInListVariable.class, "put json %jsonobject% in listvar %objects%", "put jsons %jsonobjects% in listvar %objects%");
        registerExpression(ExprListVariableAsJson.class, JSONObject.class, ExpressionType.PROPERTY, "json of listvar %objects%", "jsons of listvar %objects%");
        registerExpression(ExprStringAsJson.class, JSONObject.class, ExpressionType.PROPERTY, "json of string %string%");
        //ListUtil
        registerEffect(EffMoveItem.class, "move %objects% (-1¦front|-1¦forward[s]|1¦back[ward[s]]) %number%");
        //Miscellaneous
        registerEnum(Difficulty.class, "difficulty", Difficulty.values());
        registerEnum(PlayerLoginEvent.Result.class, "playerloginresult", PlayerLoginEvent.Result.values ());
        registerEnum(HangingBreakEvent.RemoveCause.class, "hangingremovecause", HangingBreakEvent.RemoveCause.values());
        registerEffect(EffWaitAsync.class, "async wait %timespan%");
        registerEffect(EffWait.class, "[(2¦async)] wait (0¦until|1¦while) %boolean% [for %-timespan%]");
		registerEvent("Hang Event", SimpleEvent.class, HangingPlaceEvent.class, "hang");
		registerEventValue(HangingPlaceEvent.class, Block.class, HangingPlaceEvent::getBlock);
		registerEvent("Unhang Event", EvtUnhang.class, HangingBreakEvent.class, "unhang [due to %-hangingremovecauses%]");
		registerEventValue(HangingBreakByEntityEvent.class, Entity.class, HangingBreakByEntityEvent::getRemover);
		registerEventValue(HangingBreakEvent.class, HangingBreakEvent.RemoveCause.class, HangingBreakEvent::getCause);
        registerEvent("Chat Tab Complete Event", SimpleEvent.class, PlayerChatTabCompleteEvent.class, "chat tab complete");
        registerEventValue(PlayerChatTabCompleteEvent.class, String.class, PlayerChatTabCompleteEvent::getChatMessage);
        registerEvent("Armor Stand Interact Event", SimpleEvent.class, PlayerArmorStandManipulateEvent.class, "armor stand (manipulate|interact)");
        registerEventValue(PlayerArmorStandManipulateEvent.class, ItemStack.class, PlayerArmorStandManipulateEvent::getArmorStandItem);
        registerEventValue(PlayerArmorStandManipulateEvent.class, Slot.class, e ->
                new ArmorStandEquipmentSlot(e.getRightClicked(), ArmorStandEquipmentSlot.EquipSlot.getByEquipmentSlot(e.getSlot())));
        registerEvent("Armor Stand Place Event", EvtArmorStandPlace.class, EntitySpawnEvent.class, "armor stand place");
        registerExpression(ExprLastToken.class, String.class, ExpressionType.SIMPLE, "last token");
        registerExpression(ExprHangedEntity.class,Entity.class,ExpressionType.SIMPLE,"hanged entity");
		registerExpression(ExprWorldString.class,World.class,ExpressionType.PROPERTY,"world %string%");
		registerExpression(ExprHighestSolidBlock.class,Block.class,ExpressionType.PROPERTY,"highest [(solid|non-air)] block at %location%");
		registerExpression(ExprDifficulty.class,Difficulty.class,ExpressionType.PROPERTY,"difficulty of %world%");
		registerExpression(ExprGameRule.class,String.class,ExpressionType.PROPERTY,"value of [game]rule %string% in %world%");
		registerExpression(ExprReturnTypeOfFunction.class,ClassInfo.class,ExpressionType.PROPERTY,"return type of function %string%");
        registerExpression(ExprRemainingAir.class,Timespan.class,ExpressionType.PROPERTY,"breath of %livingentity%", "%livingentity%'s breath", "max breath of %livingentity%", "%livingentity%'s max breath");
		registerExpression(ExprLoadedScripts.class,String.class,ExpressionType.SIMPLE, "loaded script[ name]s");
        registerExpression(ExprCompletions.class,String.class,ExpressionType.SIMPLE,"completions");
        registerExpression(ExprLoginResult.class, PlayerLoginEvent.Result.class, ExpressionType.SIMPLE, "(login|connect[ion]) result");
        registerExpression(ExprServerIP.class, String.class, ExpressionType.SIMPLE, "[mundo[sk]] [the] ip of server", "[mundo[sk]] [the] server's ip");
        registerExpression(ExprServerPort.class, Number.class, ExpressionType.SIMPLE, "[mundo[sk]] [the] port of server", "[mundo[sk]] [the] server's port");
        registerExpression(ExprAllTypes.class, ClassInfo.class, ExpressionType.SIMPLE, "all types");
        registerExpression(ExprEntityCanCollide.class, Boolean.class, ExpressionType.PROPERTY, "%livingentity% is collidable");
        registerExpression(ExprTreeAtLoc.class, Block.class, ExpressionType.PROPERTY, "tree at %location%");
        registerExpression(ExprThatAre.class, Object.class, ExpressionType.COMBINED, "%objects% that are %object%");
        registerExpression(ExprRespawnLocation.class, Location.class, ExpressionType.SIMPLE, "respawn location");
        registerExpression(ExprDestination.class, Location.class, ExpressionType.SIMPLE, "destination");
        registerExpression(ExprNewPortal.class, Location.class, ExpressionType.PROPERTY, "new nether portal within [[a] radius of] %number% (block|meter)s of %location%");
        registerExpression(ExprFlying.class, Boolean.class, ExpressionType.PROPERTY, "[%player% is] flying");
        registerScope(ScopeMatcher.class, "(switch|match) %object%");
        registerScope(ScopeMatches.class, "(case|matches) %object%");
        registerScope(ScopeAsync.class, "async [in %-timespan%]");
        registerScope(ScopeSync.class, "(sync|in %-timespan%)");
        registerScope(ScopeWhen.class, "when %boolean%");
        {
            //ServerListPing
            registerEvent("Server List Ping", SimpleEvent.class, ServerListPingEvent.class, "[[(server|player)] list] ping");
            registerExpression(ExprAmountOfPlayers.class, Number.class, ExpressionType.SIMPLE, "(shown|sent) (0¦amount of|1¦max [amount of]) players");
            registerExpression(ExprMotd.class, String.class, ExpressionType.SIMPLE, "(shown|sent) motd");
            registerExpression(ExprIP.class, String.class, ExpressionType.SIMPLE, "pinger's ip");
        }
        //NoteBlock
        ArrayList<Pair<String, Note>> notes = new ArrayList<>();
        for (int octave : new int[]{0, 1})
            for (Note.Tone tone : Note.Tone.values())
                for (int deviation : new int[]{-1, 0, 1}) {
                    if (deviation == 1 && (tone == Note.Tone.B || tone == Note.Tone.E)) continue;
                    if (deviation == -1 && (tone == Note.Tone.C || tone == Note.Tone.F)) continue;
                    Note note = Note.natural(octave, tone);
                    if (deviation == 1) note = note.sharped();
                    else if (deviation == -1) note = note.flattened();
                    String noteName = tone.name() + (deviation == 1 ? "+" : deviation == -1 ? "-" : "") + octave;
                    notes.add(new Pair<>("n" + noteName, note));
                    if (octave == 0) notes.add(new Pair<>("n" + noteName.substring(0, noteName.length() - 1), note));
                    if (!serverHasPlugin("RandomSK")) {
                        notes.add(new Pair<>(noteName, note));
                        if (octave == 0) notes.add(new Pair<>(noteName.substring(0, noteName.length() - 1), note));
                    }
                }
        Note fSharp2 = Note.sharp(2, Note.Tone.F);
        notes.add(new Pair<>("nF+2", fSharp2));
        notes.add(new Pair<>("nG-2", fSharp2));
        if (!serverHasPlugin("RandomSK")) {
            notes.add(new Pair<>("F+2", fSharp2));
            notes.add(new Pair<>("G-2", fSharp2));
        }
        registerEnum(Note.class, "note", new Note[0], notes.toArray(new Pair[0]));
        registerEnum(Instrument.class, "instrument", Instrument.values());
        registerEffect(EffPlayNoteBlock.class, "play [[%-note% with] %-instrument% on] noteblock %block%");
        registerEvent("Note Play", SimpleEvent.class, NotePlayEvent.class, "note play");
        registerEventValue(NotePlayEvent.class, Note.class, NotePlayEvent::getNote);
        registerEventValue(NotePlayEvent.class, Instrument.class, NotePlayEvent::getInstrument);
        registerEventValue(NotePlayEvent.class, Block.class, NotePlayEvent::getBlock);
        registerExpression(ExprNoteOfBlock.class, Note.class, ExpressionType.PROPERTY, "note of %block%", "%block%'s note");
        //Probability
		registerScope(ScopeProbability.class, "prob[ability]", "random chance");
		registerCondition(CondProbabilityValue.class, "%number% prob[ability]");
		registerExpression(ExprRandomIndex.class,String.class,ExpressionType.PROPERTY,"random from %numbers% prob[abilitie]s");
		registerExpression(ExprRandomNumberIndex.class,Integer.class,ExpressionType.PROPERTY,"random number from %numbers% prob[abilitie]s");
		//ProtocolLib
		if (serverHasPlugin("ProtocolLib")) {
            info("You've discovered the amazing realm of ProtocolLib packet syntaxes!");
            String pLibVersion = Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion();
            if (!pLibVersion.substring(0, 1).equals("4") || pLibVersion.substring(0, 3).equals("4.0")) {
                info("Your version of ProtocolLib is " + pLibVersion);
                info("MundoSK requires that you run at least version 4.1 of ProtocolLib");
                info("If you are running at least version 4.1 of ProtocolLib, please post a message on MundoSK's thread on forums.skunity.com");
            }
            registerEnum(PacketType.class, "packettype", new PacketType[0], UtilPacketEvent.nametoptype.entrySet().toArray(new Map.Entry[0]));
            registerType(PacketContainer.class, "packet");
			registerEffect(EffSendPacket.class, "send packet %packet% to %player%", "send %player% packet %packet%");
            registerEffect(EffReceivePacket.class, "rec(ei|ie)ve packet %packet% from %player%"); //Included incorrect spelling to avoid wasted time
			registerEffect(EffPacketInfo.class, "packet info %packet%");
            registerEvent("Packet Event", EvtPacketEvent.class, UtilPacketEvent.class, "packet event %packettypes%");
			registerEventValue(UtilPacketEvent.class, PacketContainer.class, UtilPacketEvent::getPacket);
			registerEventValue(UtilPacketEvent.class, PacketType.class, UtilPacketEvent::getPacketType);
			registerEventValue(UtilPacketEvent.class, Player.class, UtilPacketEvent::getPlayer);
            registerExpression(ExprTypeOfPacket.class, PacketType.class, ExpressionType.SIMPLE, "packettype of %packet%", "%packet%'s packettype");
			registerExpression(ExprNewPacket.class, PacketContainer.class, ExpressionType.PROPERTY, "new %packettype% packet");
            registerExpression(ExprJSONObjectOfPacket.class, JSONObject.class, ExpressionType.PROPERTY,
                    "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(true) + ") pjson %number% of %packet%",
                    "(%-string%" + ExprJSONObjectOfPacket.getConverterNamesPattern(false) + ") array pjson %number% of %packet%");
            registerExpression(ExprObjectOfPacket.class, Object.class, ExpressionType.PROPERTY,
                    "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(true) + ") pinfo %number% of %packet%",
                    "(0¦%-classinfo/string%" + ExprObjectOfPacket.getConverterNamesPattern(false) + ") array pinfo %number% of %packet%");
            registerExpression(ExprPrimitiveOfPacket.class, Number.class, ExpressionType.PROPERTY, "(0¦byte|1¦short|2¦int|3¦long|4¦float|5¦double) pnum %number% of %packet%");
            registerExpression(ExprPrimitiveArrayOfPacket.class, Number.class, ExpressionType.PROPERTY, "(0¦int|1¦byte) array pnum %number% of %packet%");
            registerExpression(ExprEntityOfPacket.class, Entity.class, ExpressionType.PROPERTY,
                    "%world% pentity %number% of %packet%",
                    "%world% pentity array %number% of %packet%");
            registerExpression(ExprEnumOfPacket.class, String.class, ExpressionType.PROPERTY, "%string% penum %number% of %packet%");
		}
        //Skin
        if (serverHasPlugin("ProtocolLib") && implementPacketStuff) {
            registerType(Skin.class, "skin", "skintexture").parser(new SimpleParser<Skin>() {
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
                    fields.putObject("value", skin.toJSONArray().toJSONString());
                    return fields;
                }

                @Override
                public void deserialize(Skin skin, Fields fields) throws StreamCorruptedException, NotSerializableException {
                    throw new UnsupportedOperationException("Skin does not have a nullary constructor!");
                }

                @Override
                public Skin deserialize(Fields fields) throws StreamCorruptedException, NotSerializableException {
                    try {
                        return new Skin.JSON((JSONArray) (new JSONParser()).parse((String) fields.getObject("value")));
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
            registerExpression(ExprSkinWith.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] (with|of) value %string% signature %string%");
            //registerExpression(ExprSkinOfPlayer.class, Skin.class, ExpressionType.PROPERTY, "[player] skin [texture] of %player%", "%player%'s [player] skin [texture]");
            registerExpression(ExprSkinOf.class, Skin.class, ExpressionType.PROPERTY, "skin [texture] of %player/itemstack%", "%player/itemstack%'s skin");
            registerExpression(ExprCombinedSkin.class, Skin.class, ExpressionType.PROPERTY, "(combined skin|skin combination) (from|of) %skins%", "%skins%'s (combined skin|skin combination)");
            registerExpression(ExprDisplayedSkinOfPlayer.class, Skin.class, ExpressionType.PROPERTY, "displayed skin of %player% [(for %-players%|excluding %-players%)]", "%player%'s displayed skin [(for %-players%|excluding %-players%)]");
            //registerExpression(ExprSkinOfSkull.class, Skin.class, ExpressionType.PROPERTY, "[skull] skin of %itemstack%", "%itemstack%'s [skull] skin");
            registerExpression(ExprSkullFromSkin.class, ItemStack.class, ExpressionType.PROPERTY, "skull from %skin%");
            registerExpression(ExprNameTagOfPlayer.class, String.class, ExpressionType.PROPERTY, "%player%'s name[]tag", "name[]tag of %player%");
        }
        //Socket
		registerEffect(EffWriteToSocket.class, "write %strings% to socket with host %string% port %number% [with timeout %-timespan%] [to handle response through function %-string% with id %-string%]");
		registerEffect(EffOpenFunctionSocket.class, "open function socket at port %number% [with password %-string%] [through function %-string%]");
		registerEffect(EffCloseFunctionSocket.class, "close function socket at port %number%");
		registerExpression(ExprPassOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"pass[word] of function socket at port %number%");
		registerExpression(ExprHandlerOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"handler [function] of function socket at port %number%");
		registerExpression(ExprFunctionSocketIsOpen.class,Boolean.class,ExpressionType.PROPERTY,"function socket is open at port %number%");
		registerExpression(ExprServerSocketIsOpen.class,Boolean.class,ExpressionType.COMBINED,"server socket is open at host %string% port %number% [with timeout of %-timespan%]");
		registerExpression(ExprMotdOfServer.class,String.class,ExpressionType.COMBINED,"motd of server with host %string% [port %-number%]");
		registerExpression(ExprPlayerCountOfServer.class,Number.class,ExpressionType.COMBINED,"(1¦player count|0¦max player count) of server with host %string% [port %-number%]");
		//Tablist
        if (serverHasPlugin("ProtocolLib") && implementPacketStuff) {
            registerExpression(ExprTabName.class, String.class, ExpressionType.PROPERTY, "%player%'s [mundo[sk]] tab[list] name", "[mundo[sk]] tab[list] name of %player%");
            registerType(Tablist.class, "tablist");
            Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onJoin(PlayerJoinEvent event) {
                    Tablist.onJoin(event.getPlayer());
                    SkinManager.onJoin(event.getPlayer());
                }
            }, this);
            Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onQuit(PlayerQuitEvent event) {
                    Tablist.onQuit(event.getPlayer());
                    SkinManager.onQuit(event.getPlayer());
                }
            }, this);
            registerExpression(ExprTablistContainsPlayers.class, Boolean.class, ExpressionType.PROPERTY, "(%-tablist%|%-player%'s tablist) contains players");
            registerExpression(ExprNewTablist.class, Tablist.class, ExpressionType.SIMPLE, "new tablist");
            registerExpression(ExprScoresEnabled.class, Boolean.class, ExpressionType.PROPERTY, "scores enabled in (%-tablist%|%-player%'s tablist)");
            registerExpression(ExprTablistName.class, String.class, ExpressionType.PROPERTY, "tablist name of %player% (in %-tablist%|for %-player%)", "%player%'s tablist name (in %-tablist%|for %-player%)");
            registerExpression(ExprTablistScore.class, Number.class, ExpressionType.PROPERTY, "tablist score of %player% (in %-tablist%|for %-player%)", "%player%'s tablist score (in %-tablist%|for %-player%)");
            registerEffect(EffChangePlayerVisibility.class, "(0¦show|1¦hide) %players% in (%-tablist%|tab[list] of %player%)");
            registerEffect(EffSetTablist.class, "set tablist of %players% to %tablist%", "set %player%'s tablist to %tablist%");
            {
                //Simple
                registerEffect(com.pie.tlatoani.Tablist.Simple.EffCreateNewTab.class, "create tab id %string% (in %-tablist%|for %-player%) with [display] name %string% [(ping|latency) %-number%] [(head|icon|skull) %-skin%] [score %-number%]");
                registerEffect(com.pie.tlatoani.Tablist.Simple.EffDeleteTab.class, "delete tab id %string% (in %-tablist%|for %-player%)");
                registerEffect(com.pie.tlatoani.Tablist.Simple.EffRemoveAllIDTabs.class, "delete all id tabs (in %-tablist%|for %-player%)");
                registerExpression(com.pie.tlatoani.Tablist.Simple.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of tab id %string% (in %-tablist%|for %-player%)");
                registerExpression(com.pie.tlatoani.Tablist.Simple.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of tab id %string% (in %-tablist%|for %-player%)");
                registerExpression(ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY, "(head|icon|skull) of tab id %string% (in %-tablist%|for %-player%)");
                registerExpression(com.pie.tlatoani.Tablist.Simple.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of tab id %string% (in %-tablist%|for %-player%)");
            } {
                //Array
                debug(this, "ArrayTablist.class = " + ArrayTablist.class);
                registerEffect(EffEnableArrayTablist.class, "(disable|deactivate) array tablist for %player%", "(enable|activate) array tablist for %player% [with [%-number% columns] [%-number% rows] [initial (head|icon|skull) %-skin%]]");
                registerExpression(com.pie.tlatoani.Tablist.Array.ExprDisplayNameOfTab.class, String.class, ExpressionType.PROPERTY, "[display] name of tab %number%, %number% (in %-tablist%|for %-player%)");
                registerExpression(com.pie.tlatoani.Tablist.Array.ExprLatencyOfTab.class, Number.class, ExpressionType.PROPERTY, "(latency|ping) of tab %number%, %number% (in %-tablist%|for %-player%)");
                registerExpression(com.pie.tlatoani.Tablist.Array.ExprIconOfTab.class, Skin.class, ExpressionType.PROPERTY, "(head|icon|skull) of tab %number%, %number% (in %-tablist%|for %-player%)", "initial icon of (%-tablist%|%player%'s [array] tablist)");
                registerExpression(com.pie.tlatoani.Tablist.Array.ExprScoreOfTab.class, Number.class, ExpressionType.PROPERTY, "score of tab %number%, %number% (in %-tablist%|for %-player%)");
                registerExpression(com.pie.tlatoani.Tablist.Array.ExprSizeOfTabList.class, Number.class, ExpressionType.PROPERTY, "amount of (0¦column|1¦row)s in (%-tablist%|%-player%'s [array] tablist)");
            }
        }
        //TerrainControl
		if (serverHasPlugin("TerrainControl")) {
			this.getLogger().info("You uncovered the secret TerrainControl syntaxes!");
			registerEffect(EffSpawnObject.class, "(tc|terrain control) spawn %string% at %location% with rotation %string%");
			registerExpression(ExprBiomeAt.class,String.class,ExpressionType.PROPERTY,"(tc|terrain control) biome at %location%");
			registerExpression(ExprTCEnabled.class,Boolean.class,ExpressionType.PROPERTY,"(tc|terrain control) is enabled for %world%");
		}
		//Throwable
        registerType(Throwable.class, "throwable");
        registerType(StackTraceElement.class, "stacktraceelement");
		registerScope(ScopeTry.class, "try");
        registerScope(ScopeCatch.class, "catch in %object%");
		registerEffect(EffPrintStackTrace.class, "print stack trace of %throwable%");
		registerExpression(ExprCause.class,Throwable.class,ExpressionType.PROPERTY,"throwable cause of %throwable%", "%throwable%'s throwable cause");
		registerExpression(ExprDetails.class,String.class,ExpressionType.PROPERTY,"details of %throwable%", "%throwable%'s details");
		registerExpression(ExprStackTrace.class,StackTraceElement.class,ExpressionType.PROPERTY,"stack trace of %throwable%", "%throwable%'s stack trace");
		registerExpression(ExprPropertyNameOfSTE.class,String.class,ExpressionType.PROPERTY,"(0¦class|1¦file|2¦method) name of %stacktraceelement%", "%stacktraceelement%'s (0¦class|1¦file|2¦method) name");
		registerExpression(ExprLineNumberOfSTE.class,Integer.class,ExpressionType.PROPERTY,"line number of %stacktraceelement%", "%stacktraceelement%'s line number");
		//Util
        registerExpression(ExprLoopWhile.class,Object.class,ExpressionType.PROPERTY,"%objects% (0¦while|1¦until|2¦if|3¦unless) %boolean%");
        registerExpression(ExprTreeOfListVariable.class, Object.class, ExpressionType.PROPERTY, "tree of %objects%");
        registerExpression(ExprIndexesOfListVariable.class, String.class, ExpressionType.PROPERTY, "[all [of]] [the] indexes (of|in) [value] %objects%");
        registerExpression(ExprBranch.class, String.class, ExpressionType.PROPERTY, "branch");
		//WebSocket
        registerType(WebSocket.class, "websocket");
        registerEffect(EffCloseWebSocket.class, "close websocket %websocket%");
        registerEffect(EffWebSocketSendMessage.class, "websocket send %string% [through %-websockets]");
        registerEffect(EffStartWebSocketServer.class, "start websocket server %string% at port %number%");
        registerEffect(EffStopWebSocketServer.class, "stop websocket server at port %number% [with timeout %-number%]");
        registerEventValue(WebSocketEvent.class, WebSocket.class, event -> event.webSocket);
        registerEventValue(WebSocketMessageEvent.class, String.class, event -> event.message);
        registerEventValue(WebSocketErrorEvent.class, Throwable.class, event -> event.error);
        registerExpression(ExprWebSocket.class, WebSocket.class, ExpressionType.COMBINED, "websocket %string% connected to uri %string%");
        registerExpression(ExprWebSocketServerPort.class, Number.class, ExpressionType.SIMPLE, "websocket [server] port");
        registerExpression(ExprAllWebSockets.class, WebSocket.class, ExpressionType.PROPERTY, "all websockets [of server at port %number%");
        registerExpression(ExprWebSocketHost.class, String.class, ExpressionType.PROPERTY, "local host of %websocket%", "(remote|external) host of %websocket%");
        registerExpression(ExprWebSocketPort.class, Number.class, ExpressionType.PROPERTY, "local port of %websocket%", "(remote|external) port of %websocket%");
        //WorldBorder
		registerEffect(EffResetBorder.class, "reset %world%");
		registerEvent("Border Stabilize", EvtBorderStabilize.class, UtilBorderStabilizeEvent.class, "border stabilize [in %-world%]");
		registerEventValue(UtilBorderStabilizeEvent.class, World.class, UtilBorderStabilizeEvent::getWorld);
		registerExpression(ExprSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"size of %world% [over %-timespan%]");
		registerExpression(ExprCenterOfBorder.class,Location.class,ExpressionType.PROPERTY,"center of %world%");
		registerExpression(ExprDamageAmountOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage amount of %world%");
		registerExpression(ExprDamageBufferOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage buffer of %world%");
		registerExpression(ExprWarningDistanceOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning distance of %world%");
		registerExpression(ExprWarningTimeOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning time of %world%");
		registerExpression(ExprFinalSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"final size of %world%");
		registerExpression(ExprTimeRemainingUntilBorderStabilize.class,Timespan.class,ExpressionType.PROPERTY,"time remaining until border stabilize in %world%");
		registerExpression(CondBeyondBorder.class,Boolean.class,ExpressionType.PROPERTY,"%locations% (is|are) (0¦within|1¦beyond) border");
		//WorldCreator
        registerType(WorldCreator.class, "creator").parser(new SimpleParser<WorldCreator>() {
            @Override
            public WorldCreator parse(String s, ParseContext parseContext) {
                return null;
            }

            @Override
            public String toString(WorldCreator creator, int flags) {
                JSONObject jsonObject = UtilWorldLoader.getCreatorJSON(creator);
                jsonObject.put("worldname", creator.name());
                return jsonObject.toString();
            }

        });
        if (!serverHasPlugin("RandomSK")) {
            registerEnum(Environment.class, "environment", Environment.values(), new Pair<String, Environment>("END", Environment.THE_END));
        }
        registerEnum(WorldType.class, "worldtype", WorldType.values(), new Pair<String, WorldType>("SUPERFLAT", WorldType.FLAT), new Pair<String, WorldType>("LARGE BIOMES", WorldType.LARGE_BIOMES), new Pair<String, WorldType>("VERSION 1.1", WorldType.VERSION_1_1));
		registerExpression(ExprCreatorNamed.class,WorldCreator.class,ExpressionType.PROPERTY,"creator (with name|named) %string%");
		registerExpression(ExprCreatorWith.class,WorldCreator.class,ExpressionType.PROPERTY,"%creator%[ modified],[ name %-string%][,][ (environment|env[ironment]) %-environment%][,][ seed %-string%][,][ type %-worldtype%][,][ gen[erator] %-string%][,][ gen[erator] settings %-string%][,][ struct[ures] %-boolean%]");
		registerExpression(ExprCreatorOf.class,WorldCreator.class,ExpressionType.PROPERTY,"creator of %world%");
		registerExpression(ExprNameOfCreator.class,String.class,ExpressionType.PROPERTY,"worldname of %creator%");
		registerExpression(ExprEnvOfCreator.class,Environment.class,ExpressionType.PROPERTY,"env[ironment] of %creator%");
		registerExpression(ExprSeedOfCreator.class,String.class,ExpressionType.PROPERTY,"seed of %creator%");
		registerExpression(ExprGenOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] of %creator%");
		registerExpression(ExprGenSettingsOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] setSafely[tings] of %creator%");
		registerExpression(ExprTypeOfCreator.class,WorldType.class,ExpressionType.PROPERTY,"worldtype of %creator%");
		registerExpression(ExprStructOfCreator.class,Boolean.class,ExpressionType.PROPERTY,"struct[ure(s| settings)] of %creator%");
		//WorldManagement
        Converters.registerConverter(World.class, WorldCreator.class, new Converter<World, WorldCreator>() {
            @Override
            public WorldCreator convert(World world) {
                WorldCreator worldCreator = new WorldCreator(world.getName());
                worldCreator.copy(world);
                worldCreator.type(world.getWorldType());
                worldCreator.generateStructures(world.canGenerateStructures());
                worldCreator.generatorSettings("");
                return worldCreator;
            }
        });
        registerEffect(EffCreateWorld.class, "create world named %string%[,][ (environment|env[ironment]) %-environment%][,][ seed %-string%][,][ type %-worldtype%][,][ gen[erator] %-string%][,][ gen[erator] settings %-string%][,][ struct[ures] %-boolean%]");
		registerEffect(EffCreateWorldCreator.class, "create world using %creator%");
		registerEffect(EffUnloadWorld.class, "unload %world% [save %-boolean%]");
		registerEffect(EffDeleteWorld.class, "delete %world%");
		registerEffect(EffDuplicateWorld.class, "duplicate %world% using name %string%");
        registerExpression(ExprCurrentWorlds.class,World.class,ExpressionType.SIMPLE,"[all] current worlds");
        //WorldLoader
        registerEffect(EffRunCreatorOnStart.class, "run %creator% on start"); //Will be removed in a future version
        registerEffect(EffDoNotLoadWorldOnStart.class, "don't load world %string% on start"); //Will be removed in a future version

        registerExpression(ExprAllAutomaticCreators.class, WorldCreator.class, ExpressionType.SIMPLE, "[all] automatic creators");
        registerExpression(ExprAutomaticCreator.class, WorldCreator.class, ExpressionType.SIMPLE, "automatic creator %string%");
        //ZExperimental - The Z is for mystery (it's so that it appears last in the package list)
        registerEvent("Custom Element Event", CustomEffect.class, CustomElementEvent.class, "[jsoup_light] [new] [custom] effect %string%");
        //
        ArrayList<String> patterns = new ArrayList<>();
        for (String s : enumNames) {
            patterns.add("[all] " + s + "s");
        }
        Skript.registerExpression(ExprEnumValues.class, Object.class, ExpressionType.SIMPLE, patterns.toArray(new String[0]));
		try {
			Field classinfos = Classes.class.getDeclaredField("tempClassInfos");
			classinfos.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<ClassInfo<?>> classes = (List<ClassInfo<?>>) classinfos.get(null);
			for (int i = 0; i < classes.size(); i++)
				registerCustomEventValue(classes.get(i));
		} catch (Exception e1) {
			reportException(this, e1);
		}
        if (Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11")) {
		    RegistryPreColorUpdate.register();
        }
        if (Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11") || Bukkit.getVersion().contains("1.12")) {
            RegistryFromCombatUpdate.register();
        }
        ListUtil.register();
        ExprEventSpecificValue.register();
		info("Awesome syntaxes have been registered!");
        sync(Mundo::enableMetrics);
	}

    @Override
    public void onDisable() {
        UtilFunctionSocket.onDisable();
        info("Closed all function sockets (if any were open)");
        try {
            UtilWorldLoader.save();
            info("Successfully saved all world loaders");
        } catch (IOException e) {
            info("A problem occurred while saving world loaders");
            reportException(this, e);
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String unusedWorldName, String id) {
        return SkriptGeneratorManager.getSkriptGenerator(id);
    }
    
    //Registration
    
    public static void registerEffect(Class<? extends Effect> effectClass, String... patterns) {
        Skript.registerEffect(effectClass, patterns);
    }
    
    public static <T> void registerExpression(Class<? extends Expression<T>> expressionClass, Class<T> type, ExpressionType expressionType, String... patterns) {
        Skript.registerExpression(expressionClass, type, expressionType, patterns);
    }

    public static void registerCondition(Class<? extends Condition> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }
    
    public static void registerEvent(String name, Class<? extends SkriptEvent> eventClass, Class<? extends Event> eventType, String... patterns) {
        Skript.registerEvent(name, eventClass, eventType, patterns);
    }

    public static void registerScope(Class<? extends CustomScope> conditionClass, String... patterns) {
        Skript.registerCondition(conditionClass, patterns);
    }

    public static <E extends Event, R> void registerEventValue(Class<E> tClass, Class<R> rClass, Function<E, R> function) {
	    EventValues.registerEventValue(tClass, rClass, new Getter<R, E>() {
            @Override
            public R get(E event) {
                return function.apply(event);
            }
        }, 0);
    }

    public static <T> ClassInfo<T> registerType(Class<T> type, String name, String... alternateNames) {
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(alternateNames));
        names.add(0, name);
        ClassInfo<T> result = new ClassInfo<T>(type, name).user(names.toArray(new String[0])).name(name).parser(new SimpleParser<T>() {
            @Override
            public T parse(String s, ParseContext parseContext) {
                return null;
            }
        });
        if (classInfoSafe(type, name)) {
            Classes.registerClass(result);
        }
        return result;
    }

    //Default pairing string names should be in uppercase
    public static <E> void registerEnum(Class<E> enumClass, String name, E[] values, Map.Entry<String, E>... defaultPairings) {
        if (!classInfoSafe(enumClass, name)) return;
        Classes.registerClass(new ClassInfo<E>(enumClass, name).user(new String[]{name}).name(name).parser(new Parser<E>() {
            private E[] enumValues = values;
            private Map.Entry<String, E>[] additionalPairings = defaultPairings;

            @Override
            public E parse(String s, ParseContext parseContext) {
                String upperCase = s.toUpperCase();
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getKey().equals(upperCase)) {
                        return additionalPairings[i].getValue();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].toString().equals(upperCase)) {
                        return values[i];
                    }
                }
                return null;
            }

            @Override
            public String toString(E e, int useless) {
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getValue() == e) {
                        return additionalPairings[i].getKey().toLowerCase();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == e) {
                        return values[i].toString().toLowerCase();
                    }
                }
                return null;
            }

            @Override
            public String toVariableNameString(E e) {
                return toString(e, 0);
            }

            @Override
            public String getVariableNamePattern() {
                return ".+";
            }
        }).serializer(new Serializer<E>() {
            private E[] enumValues = values;
            private Map.Entry<String, E>[] additionalPairings = defaultPairings;

            public E parse(String s) {
                String upperCase = s.toUpperCase();
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getKey().equals(upperCase)) {
                        return additionalPairings[i].getValue();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i].toString().equals(upperCase)) {
                        return values[i];
                    }
                }
                return null;
            }

            public String toString(E e) {
                for (int i = 0; i < additionalPairings.length; i++) {
                    if (additionalPairings[i].getValue() == e) {
                        return additionalPairings[i].getKey().toLowerCase();
                    }
                }
                for (int i = 0; i < values.length; i++) {
                    if (values[i] == e) {
                        return values[i].toString().toLowerCase();
                    }
                }
                return null;
            }

            @Override
            public Fields serialize(E e) throws NotSerializableException {
                Fields fields = new Fields();
                fields.putObject("value", toString(e));
                return null;
            }

            @Override
            public void deserialize(E e, Fields fields) throws StreamCorruptedException, NotSerializableException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean mustSyncDeserialization() {
                return false;
            }

            @Override
            protected boolean canBeInstantiated() {
                return false;
            }

            @Override
            public E deserialize(Fields fields) throws StreamCorruptedException {
                return parse((String) fields.getObject("value"));
            }
        }));
        Set<E> allValues = new HashSet<E>();
        allValues.addAll(Arrays.asList(values));
        for (Map.Entry<String, E> entry : defaultPairings) {
            allValues.add(entry.getValue());
        }
        ena.add(allValues.toArray(new Object[0]));
        enumNames.add(name);
        enumClasses.add(enumClass);
    }

    public static class ExprEnumValues extends SimpleExpression<Object> {
        private int whichEnum;

        @Override
        protected Object[] get(Event event) {
            return Mundo.ena.get(whichEnum);
        }

        @Override
        public boolean isSingle() {
            return false;
        }

        @Override
        public Class<? extends Object> getReturnType() {
            return enumClasses.get(whichEnum);
        }

        @Override
        public String toString(Event event, boolean b) {
            return "all " + enumNames.get(whichEnum) + "s";
        }

        @Override
        public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
            whichEnum = i;
            return true;
        }
    }

    public static abstract class SimpleParser<T> extends Parser<T> {

        @Override
        public String toString(T t, int flags) {
            return t.toString();
        }

        @Override
        public String toVariableNameString(T t) {
            return toString(t, 0);
        }

        @Override
        public String getVariableNamePattern() {
            return ".+";
        }
    }

    //Metrics Util

    public static void enableMetrics() {
        try {
            Metrics metrics = new Metrics(instance);
            //Skript Version
            Graph skriptVersion = metrics.createGraph("Skript Version");
            skriptVersion.addPlotter(new Metrics.Plotter(Bukkit.getServer().getPluginManager().getPlugin("Skript").getDescription().getVersion()){
                @Override
                public int getValue() {
                    return 1;
                }
            });

            Graph addons = metrics.createGraph("Skript Addons");
            SkriptAddon[] addonlist = Skript.getAddons().toArray(new SkriptAddon[0]);
            for (int i = 0; i < addonlist.length; i++) {
                addons.addPlotter(new Metrics.Plotter((addonlist[i]).getName()) {

                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
            }

            Graph plugins = metrics.createGraph("Plugins");
            Plugin[] pluginlist = Bukkit.getPluginManager().getPlugins();
            for (int i = 0; i < pluginlist.length; i++) {
                plugins.addPlotter(new Metrics.Plotter((pluginlist[i]).getName()) {

                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
            }

            if (serverHasPlugin("ProtocolLib")) {
                Graph protocolLibVersion = metrics.createGraph("ProtocolLib Version");
                protocolLibVersion.addPlotter(new Plotter(Bukkit.getPluginManager().getPlugin("ProtocolLib").getDescription().getVersion()) {
                    @Override
                    public int getValue() {
                        return 1;
                    }
                });
            }

            metrics.start();
            info("Metrics have been enabled!");
        } catch (Exception e) {
            info("Metrics failed to enable");
            Mundo.reportException(Mundo.class, e);
        }
    }

    //Checker Util

    public static <T> boolean check(Expression<T> expression, Event event, Function<T, Boolean> function) {
	    return expression.check(event, new Checker<T>() {
            @Override
            public boolean check(T t) {
                return function.apply(t);
            }
        });
    }

    //Logging Util

    public static boolean classDebugs(Class c) {
        return debugPackages.contains(c.getName().split("\\.")[3]);
    }

    public static void info(String s) {
        Mundo.instance.getLogger().info(s);
    }
	
	public static void reportException(Object obj, Exception e) {
		info("An exception has occured within MundoSK");
		info("Please report this to the MundoSK thread on forums.skunity.com");
		info("Exception at " + (obj instanceof Class ? (Class) obj : obj.getClass()).getSimpleName());
		e.printStackTrace();
	}
	
	public static void debug(Object obj, String msg) {
        Class debugClass = obj instanceof Class ? (Class) obj : obj.getClass();
        if (classDebugs(debugClass)) {
            info("DEBUG " + debugClass.getSimpleName() + ": " + msg);
        }
	}

    public static void debug(Object obj, Exception e) {
        Class debugClass = obj instanceof Class ? (Class) obj : obj.getClass();
		if (classDebugs(debugClass)) {
			reportException(obj, e);
            info("DEBUG");
            info("An exception was reported for debugging while debug_mode was activated in the config");
            info("If you were told to activate debug_mode to help fix bugs in MundoSK on forums.skunity.com, then please copy and paste this message along with the full stack trace of the following error to hastebin.com and give the hastebin link to whoever is helping you fix this bug");
            info("If you are trying to fix a problem in MundoSK yourself, good luck :)");
            info("Otherwise, if you do not know why you are seeing this error here, go to the MundoSK config, set debug_mode to false, and restart your server");
            info("For help, go to the MundoSK thread on forums.skunity.com");
            info("Exception debugged at " + debugClass.getSimpleName());
            e.printStackTrace();
		}
	}

    //Custom Event Util
	
	public static <T> void registerCustomEventValue(ClassInfo<T> type) {
		EventValues.registerEventValue(UtilCustomEvent.class, type.getC(), new Getter<T, UtilCustomEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public T get(UtilCustomEvent e) {
				return (T) e.getDetail(type);
			}
		}, 0);
	}

    public static Boolean classInfoSafe(Class c, String name) {
        return Classes.getExactClassInfo(c) == null && Classes.getClassInfoNoError(name) == null;
    }

    //Math Util

    public static int intMod(int number, int mod) {
        return (number % mod) + (number >= 0 ? 0 : mod);
    }

    public static int limitToRange(int min, int num, int max) {
        if (num > max) return max;
        if (num < min) return min;
        return num;
    }

    public static boolean isInRange(double min, double num, double max) {
        return !(num > max || num < min);
    }

    public static char toHexDigit(int num) {
        return hexDigits.charAt(num % 16);
    }

    public static int divideNoRemainder(int dividend, int divisor) {
        return (dividend - (dividend % divisor)) / divisor;
    }

    public static int digitsInBase(int num, int base) {
        int result = 0;
        while (num > 0) {
            num /= base;
            result++;
        }
        return result;
    }

    //ListVariable Util

    public static TreeMap<String, Object> listVariableFromArray(Object[] array) {
        TreeMap<String, Object> result = new TreeMap<>();
        for (int i = 1; i <= array.length; i++) {
            if (array[i] instanceof Object[]) {
                result.put(i + "::*", listVariableFromArray((Object[]) array[i]));
            } else if (array[i] instanceof TreeMap) {
                result.put(i + "::*", array[i]);
            } else {
                result.put(i + "", array[i]);
            }
        }
        return result;
    }

    public static void setListVariable(String varname, TreeMap<String, Object> value, Event event, boolean isLocal) {
        value.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                if (o instanceof TreeMap) {
                    setListVariable(varname + "::" + s, (TreeMap<String, Object>) o, event, isLocal);
                } else {
                    Variables.setVariable(varname + "::" + s, o, event, isLocal);
                }
            }
        });
    }

    //Scheduler Util

    public static void sync(Runnable runnable) {
        scheduler.runTask(instance, runnable);
    }

    public static void async(Runnable runnable) {
        scheduler.runTaskAsynchronously(instance, runnable);
    }

    public static void syncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLater(Mundo.instance, runnable, ticks);
    }

    public static void asyncDelay(int ticks, Runnable runnable) {
        scheduler.runTaskLaterAsynchronously(Mundo.instance, runnable, ticks);
    }

    //Miscellanous

    public static boolean serverHasPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName) != null;
    }

    public static boolean classesCompatible(Class c1, Class c2) {
        return c1.isAssignableFrom(c2) || c2.isAssignableFrom(c1);
    }

    public static Class commonSuperClass(Class... classes) {
	    switch (classes.length) {
            case 0: return Object.class;
            case 1: return classes[0];
            case 2: {
                while (!classes[0].isAssignableFrom(classes[1])) {
                    classes[0] = classes[0].getSuperclass();
                }
                return classes[0];
            }
        }
        Class[] classesTail = new Class[classes.length - 1];
	    System.arraycopy(classes, 0, classesTail, 0, classes.length - 1);
        return commonSuperClass(classes[0], commonSuperClass(classesTail));
    }

    public static boolean settableTo(Expression settee) {return false;}

    //Reflection Util

    public static Object getStaticField(Class<?> location, String name) throws NoSuchFieldException, IllegalAccessException {
	    Field field = location.getField(name);
	    field.setAccessible(true);
	    return field.get(null);
    }

    //Functional Util

    public static <T, R> R[] mapArray(Function<T, R> function, T[] input) {
        return (R[]) Stream.of(input).map(function).collect(Collectors.toList()).toArray();
    }
	
}
