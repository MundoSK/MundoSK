package com.pie.tlatoani;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.pie.tlatoani.Achievement.*;
import com.pie.tlatoani.Book.*;
import com.pie.tlatoani.EnchantedBook.*;
import com.pie.tlatoani.Miscellaneous.*;
import com.pie.tlatoani.Probability.*;
import com.pie.tlatoani.Socket.*;
import com.pie.tlatoani.TerrainControl.*;
import com.pie.tlatoani.Throwable.*;
import com.pie.tlatoani.Util.*;
import com.pie.tlatoani.WorldBorder.*;
import com.pie.tlatoani.WorldCreator.*;
import com.pie.tlatoani.WorldManagement.*;
import com.pie.tlatoani.Metrics.*;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.EnchantmentType;
import ch.njol.skript.util.Getter;
import ch.njol.skript.util.Timespan;

public class Mundo extends JavaPlugin{
	public static Mundo instance;
	public static FileConfiguration config;
	
	public void onEnable(){
		instance = this;
		config = getConfig();
		config.addDefault("debug_mode", false);
		config.options().copyDefaults(true);
		saveConfig();
		
		//if (Bukkit.getServer().getPluginManager().)
		//ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.values()) {
		//	@Override
		//	public void onPacketSending(PacketEvent event) {
		//		
		//	}
		//});

		Skript.registerAddon(this);
		info("Pie is awesome :D");
		//Achievement
		Classes.registerClass(new ClassInfo<Achievement>(Achievement.class, "achievement").user(new String[]{"achievement"}).name("achievement").parser(new Parser<Achievement>(){

            public Achievement parse(String s, ParseContext context) {
            	try {
            		return Achievement.valueOf(s.toUpperCase());
            	} catch (IllegalArgumentException e) {
            		return null;
            	}
            }

            public String toString(Achievement ach, int flags) {
        		return ach.toString();
            }

            public String toVariableNameString(Achievement ach) {
        		return ach.toString();
            }

            public String getVariableNamePattern() {
                return ".+";
            }
        }));
		Skript.registerEffect(EffAwardAch.class, "award achieve[ment] %achievement% to %player%");
		Skript.registerEffect(EffRemoveAch.class, "remove achieve[ment] %achievement% from %player%");
		Skript.registerEvent("Achievement Award", EvtAchAward.class, PlayerAchievementAwardedEvent.class, "achieve[ment] [%-achievement%] award", "award of achieve[ment] [%-achievement%]");
		EventValues.registerEventValue(PlayerAchievementAwardedEvent.class, Player.class, new Getter<Player, PlayerAchievementAwardedEvent>() {
			public Player get(PlayerAchievementAwardedEvent e) {
				return e.getPlayer();
			}
		}, 0);
		EventValues.registerEventValue(PlayerAchievementAwardedEvent.class, Achievement.class, new Getter<Achievement, PlayerAchievementAwardedEvent>() {
			public Achievement get(PlayerAchievementAwardedEvent e) {
				return e.getAchievement();
			}
		}, 0);
		Skript.registerExpression(ExprParentAch.class,Achievement.class,ExpressionType.PROPERTY,"parent of achieve[ment] %achievement%");
		Skript.registerExpression(ExprAllAch.class,Achievement.class,ExpressionType.PROPERTY,"[all] achieve[ment]s [of %-player%]", "%player%'s achieve[ment]s");
		Skript.registerExpression(ExprHasAch.class,Boolean.class,ExpressionType.PROPERTY,"%player% has achieve[ment] %achievement%");
		//Book
		Skript.registerEffect(EffAddPage.class, "(add|insert|write) %strings% (1�before|0�after) (page %-number%|last page) (of|in) %itemstack%");
		Skript.registerEffect(EffMovePage.class, "move (page[s] %-number% [to %-number%]|last [%-number%] page[s]) (of|in) %itemstack% (-1�front|-1�forward[s]|1�back[ward[s]]) %number%");
		Skript.registerExpression(ExprBook.class,ItemStack.class,ExpressionType.COMBINED,"%itemstack% titled %string%, [written] by %string%, [with] %number% page[s] [%-strings%]");
		Skript.registerExpression(ExprTitleOfBook.class,String.class,ExpressionType.PROPERTY,"title of %itemstack%");
		Skript.registerExpression(ExprAuthorOfBook.class,String.class,ExpressionType.PROPERTY,"author of %itemstack%");
		Skript.registerExpression(ExprPageOfBook.class,String.class,ExpressionType.PROPERTY,"(page %number%|last page) of %itemstack%");
		Skript.registerExpression(ExprPagesOfBook.class,String.class,ExpressionType.PROPERTY,"pages [%-number% to (%-number%|last)] of %itemstack%");
		Skript.registerExpression(ExprPageCountOfBook.class,Integer.class,ExpressionType.PROPERTY,"page count of %itemstack%");
		//EnchantedBook
		Skript.registerExpression(ExprEnchBookWithEnch.class,ItemStack.class,ExpressionType.PROPERTY,"%itemstack% containing %enchantmenttypes%");
		Skript.registerExpression(ExprEnchantLevelInEnchBook.class,Integer.class,ExpressionType.PROPERTY,"level of %enchantmenttype% within %itemstack%");
		Skript.registerExpression(ExprEnchantsInEnchBook.class,EnchantmentType.class,ExpressionType.PROPERTY,"enchants within %itemstack%");
		//Miscellaneous
		Classes.registerClass(new ClassInfo<Difficulty>(Difficulty.class, "difficulty").user(new String[]{"difficulty"}).name("difficulty").parser(new Parser<Difficulty>(){

            public Difficulty parse(String s, ParseContext context) {
            	try {
            		return Difficulty.valueOf(s.toUpperCase());
            	} catch (IllegalArgumentException e) {
            		return null;
            	}
            }

            public String toString(Difficulty diff, int flags) {
        		return diff.toString().toLowerCase();
            }

            public String toVariableNameString(Difficulty diff) {
        		return diff.toString().toLowerCase();
            }

            public String getVariableNamePattern() {
                return ".+";
            }
        }));
		Skript.registerExpression(ExprWorldString.class,World.class,ExpressionType.PROPERTY,"world %string%");
		Skript.registerExpression(ExprHighestSolidBlock.class,Block.class,ExpressionType.PROPERTY,"highest [(solid|non-air)] block at %location%");
		Skript.registerExpression(ExprDifficulty.class,Difficulty.class,ExpressionType.PROPERTY,"difficulty of %world%");
		Skript.registerExpression(ExprGameRule.class,String.class,ExpressionType.PROPERTY,"value of [game]rule %string% in %world%");
		Skript.registerExpression(ExprReturnTypeOfFunction.class,ClassInfo.class,ExpressionType.PROPERTY,"return type of function %string%");
        Skript.registerExpression(ExprRemainingAir.class,Timespan.class,ExpressionType.PROPERTY,"breath of %livingentity%", "%livingentity%'s breath");
		//Probability
		Skript.registerCondition(ScopeProbability.class, "prob[ability]", "random chance");
		Skript.registerCondition(CondProbability.class, "%number%[1�\\%] prob[ability]");
		Skript.registerExpression(ExprRandomIndex.class,String.class,ExpressionType.PROPERTY,"random from %numbers% prob[abilitie]s");
		Skript.registerExpression(ExprRandomNumberIndex.class,Integer.class,ExpressionType.PROPERTY,"random number from %numbers% prob[abilitie]s");
		//Socket
		Skript.registerEffect(EffWriteToSocket.class, "write (0�%-strings%|1�bytes %-numbers%) to socket with host %string% port %number% [with timeout %-timespan%] [to handle response through function %-string% with id %-string%]");
		Skript.registerEffect(EffOpenFunctionSocket.class, "open function socket at port %number% [with password %-string%] [through function %-string%]");
		Skript.registerEffect(EffCloseFunctionSocket.class, "close function socket at port %number%");
		Skript.registerExpression(ExprPassOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"pass[word] of function socket at port %number%");
		Skript.registerExpression(ExprHandlerOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"handler [function] of function socket at port %number%");
		Skript.registerExpression(ExprFunctionSocketIsOpen.class,Boolean.class,ExpressionType.PROPERTY,"function socket is open at port %number%");
		Skript.registerExpression(ExprServerSocketIsOpen.class,Boolean.class,ExpressionType.COMBINED,"server socket is open at host %string% port %number% [with timeout of %-timespan%]");
		Skript.registerExpression(ExprMotdOfServer.class,String.class,ExpressionType.COMBINED,"motd of server with host %string% [port %-number%]");
		Skript.registerExpression(ExprPlayerCountOfServer.class,Number.class,ExpressionType.COMBINED,"(1�player count|0�max player count) of server with host %string% [port %-number%]");
		//TerrainControl
		if (Bukkit.getServer().getPluginManager().getPlugin("TerrainControl") != null) {
			this.getLogger().info("You uncovered the secret TerrainControl syntaxes!");
			Skript.registerEffect(EffSpawnObject.class, "(tc|terrain control) spawn %string% at %location% with rotation %string%");
			Skript.registerExpression(ExprBiomeAt.class,String.class,ExpressionType.PROPERTY,"(tc|terrain control) biome at %location%");
			Skript.registerExpression(ExprTCEnabled.class,Boolean.class,ExpressionType.PROPERTY,"(tc|terrain control) is enabled for %world%");
		}
		//Throwable
		Classes.registerClass(new ClassInfo<Throwable>(Throwable.class, "throwable").user(new String[]{"throwable"}).name("throwable").parser(new Parser<Throwable>(){

            public Throwable parse(String s, ParseContext context) {
                return null;
            }

            public String toString(Throwable exc, int flags) {
                return exc.toString();
            }

            public String toVariableNameString(Throwable exc) {
                return exc.toString();
            }

            public String getVariableNamePattern() {
                return ".+";
            }
        }));
		Classes.registerClass(new ClassInfo<StackTraceElement>(StackTraceElement.class, "stacktraceelement").user(new String[]{"stacktraceelement"}).name("stacktraceelement").parser(new Parser<StackTraceElement>(){

            public StackTraceElement parse(String s, ParseContext context) {
                return null;
            }

            public String toString(StackTraceElement elem, int flags) {
                return elem.toString();
            }

            public String toVariableNameString(StackTraceElement elem) {
                return elem.toString();
            }

            public String getVariableNamePattern() {
                return ".+";
            }
        }));
		Skript.registerCondition(ScopeTry.class, "try");
		Skript.registerEffect(EffPrintStackTrace.class, "print stack trace of %throwable%");
		if (Bukkit.getServer().getPluginManager().getPlugin("RandomSK") == null)
		Skript.registerExpression(ExprCatch.class,Throwable.class,ExpressionType.SIMPLE,"(catch|caught exception)");
		else
		Skript.registerExpression(ExprCatch.class,Throwable.class,ExpressionType.SIMPLE,"caught exception");
		Skript.registerExpression(ExprCause.class,Throwable.class,ExpressionType.PROPERTY,"throwable cause of %throwable%", "%throwable%'s throwable cause");
		Skript.registerExpression(ExprDetails.class,String.class,ExpressionType.PROPERTY,"details of %throwable%", "%throwable%'s details");
		Skript.registerExpression(ExprStackTrace.class,StackTraceElement.class,ExpressionType.PROPERTY,"stack trace of %throwable%", "%throwable%'s stack trace");
		Skript.registerExpression(ExprPropertyNameOfSTE.class,String.class,ExpressionType.PROPERTY,"(0�class|1�file|2�method) name of %stacktraceelement%", "%stacktraceelement%'s (0�class|1�file|2�method) name");
		Skript.registerExpression(ExprLineNumberOfSTE.class,Integer.class,ExpressionType.PROPERTY,"line number of %stacktraceelement%", "%stacktraceelement%'s line number");
		//Util
		Skript.registerEffect(EffScope.class, "$ scope");
		Skript.registerEffect(EffCallCustomEvent.class, "call custom event %string% [to] [det[ail]s %-objects%] [arg[ument]s %-objects%]");
		Skript.registerEvent("Custom Event", EvtCustomEvent.class, UtilCustomEvent.class, "[custom] (event|evt) [%-string%]");
		Skript.registerExpression(ExprIDOfCustomEvent.class,String.class,ExpressionType.PROPERTY,"id of custom event", "custom event's id");
		Skript.registerExpression(ExprArgsOfCustomEvent.class,Object.class,ExpressionType.PROPERTY,"args of custom event", "custom event's args");
		//WorldBorder
		Skript.registerEffect(EffResetBorder.class, "reset %world%");
		Skript.registerEvent("Border Stabilize", EvtBorderStabilize.class, UtilBorderStabilizeEvent.class, "border stabilize [in %-world%]");
		EventValues.registerEventValue(UtilBorderStabilizeEvent.class, World.class, new Getter<World, UtilBorderStabilizeEvent>() {
			@Override
			public World get(UtilBorderStabilizeEvent e) {
				return e.getWorld();
			}
		}, 0);
		Skript.registerExpression(ExprSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"size of %world% [over %-timespan%]");
		Skript.registerExpression(ExprCenterOfBorder.class,Location.class,ExpressionType.PROPERTY,"center of %world%");
		Skript.registerExpression(ExprDamageAmountOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage amount of %world%");
		Skript.registerExpression(ExprDamageBufferOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage buffer of %world%");
		Skript.registerExpression(ExprWarningDistanceOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning distance of %world%");
		Skript.registerExpression(ExprWarningTimeOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning time of %world%");
		Skript.registerExpression(ExprFinalSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"final size of %world%");
		Skript.registerExpression(ExprTimeRemainingUntilBorderStabilize.class,Timespan.class,ExpressionType.PROPERTY,"time remaining until border stabilize in %world%");
		Skript.registerExpression(ExprBeyondBorder.class,Boolean.class,ExpressionType.PROPERTY,"%location% is (1�within|0�beyond) border");
		//WorldCreator
		Classes.registerClass(new ClassInfo<WorldCreator>(WorldCreator.class, "creator").user(new String[]{"creator"}).name("creator").parser(new Parser<WorldCreator>(){

            public WorldCreator parse(String s, ParseContext context) {
                return null;
            }

            public String toString(WorldCreator creator, int flags) {
                return creator.toString();
            }

            public String toVariableNameString(WorldCreator creator) {
                return creator.toString();
            }

            public String getVariableNamePattern() {
                return ".+";
            }
        }));
		if (Bukkit.getServer().getPluginManager().getPlugin("RandomSK") == null) {
			Classes.registerClass(new ClassInfo<Environment>(Environment.class, "environment").user(new String[]{"environment"}).name("environment").parser(new Parser<Environment>(){

	            public Environment parse(String s, ParseContext context) {
	            	if (s.equalsIgnoreCase("NORMAL")) return (World.Environment.NORMAL);
	    			if (s.equalsIgnoreCase("NETHER")) return (World.Environment.NETHER);
	    			if (s.equalsIgnoreCase("END") || s.equalsIgnoreCase("THE_END")) return (World.Environment.THE_END);
	                return null;
	            }

	            public String toString(Environment env, int flags) {
	        		if (env == World.Environment.NORMAL) return "normal";
	        		if (env == World.Environment.NETHER) return "nether";
	        		if (env == World.Environment.THE_END) return "end";
	        		return null;
	            }

	            public String toVariableNameString(Environment env) {
	            	if (env == World.Environment.NORMAL) return "normal";
	        		if (env == World.Environment.NETHER) return "nether";
	        		if (env == World.Environment.THE_END) return "end";
	        		return null;
	            }

	            public String getVariableNamePattern() {
	                return ".+";
	            }
	        }));
		}
		Classes.registerClass(new ClassInfo<WorldType>(WorldType.class, "worldtype").user(new String[]{"worldtype"}).name("worldtype").parser(new Parser<WorldType>(){

            public WorldType parse(String s, ParseContext context) {
            	if (s.equalsIgnoreCase("normal")) return (WorldType.NORMAL);
    			if (s.equalsIgnoreCase("flat") || s.equalsIgnoreCase("superflat")) return (WorldType.FLAT);
    			if (s.equalsIgnoreCase("large biomes") || s.equalsIgnoreCase("large_biomes")) return (WorldType.LARGE_BIOMES);
    			if (s.equalsIgnoreCase("amplified")) return (WorldType.AMPLIFIED);
    			if (s.equalsIgnoreCase("version 1.1") || s.equalsIgnoreCase("version_1_1")) return (WorldType.VERSION_1_1);
    			if (s.equalsIgnoreCase("customized")) return (WorldType.CUSTOMIZED);
                return null;
            }

            public String toString(WorldType type, int flags) {
            	if (type == WorldType.NORMAL) return "normal";
        		if (type == WorldType.AMPLIFIED) return "amplified";
        		if (type == WorldType.FLAT) return "flat";
        		if (type == WorldType.LARGE_BIOMES) return "large biomes";
        		if (type == WorldType.VERSION_1_1) return "version 1.1";
        		if (type == WorldType.CUSTOMIZED) return "customized";
        		return null;
            }

            public String toVariableNameString(WorldType type) {
            	if (type == WorldType.NORMAL) return "normal";
        		if (type == WorldType.AMPLIFIED) return "amplified";
        		if (type == WorldType.FLAT) return "flat";
        		if (type == WorldType.LARGE_BIOMES) return "large biomes";
        		if (type == WorldType.VERSION_1_1) return "version 1.1";
        		if (type == WorldType.CUSTOMIZED) return "customized";
        		return null;
            }

            public String getVariableNamePattern() {
                return ".+";
            }
        }));
		Skript.registerExpression(ExprCreatorNamed.class,WorldCreator.class,ExpressionType.PROPERTY,"creator (with name|named) %string%");
		Skript.registerExpression(ExprCreatorWith.class,WorldCreator.class,ExpressionType.PROPERTY,"%creator%[ modified],[ name %-string%][,][ env[ironment] %-environment%][,][ seed %-string%][,][ type %-worldtype%][,][ gen[erator] %-string%][,][ gen[erator] settings %-string%][,][ struct[ures] %-boolean%]");
		Skript.registerExpression(ExprCreatorOf.class,WorldCreator.class,ExpressionType.PROPERTY,"creator of %world%");
		Skript.registerExpression(ExprNameOfCreator.class,String.class,ExpressionType.PROPERTY,"worldname of %creator%");
		Skript.registerExpression(ExprEnvOfCreator.class,Environment.class,ExpressionType.PROPERTY,"env[ironment] of %creator%");
		Skript.registerExpression(ExprSeedOfCreator.class,String.class,ExpressionType.PROPERTY,"seed of %creator%");
		Skript.registerExpression(ExprGenOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] of %creator%");
		Skript.registerExpression(ExprGenSettingsOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] set[tings] of %creator%");
		Skript.registerExpression(ExprTypeOfCreator.class,WorldType.class,ExpressionType.PROPERTY,"worldtype of %creator%");
		Skript.registerExpression(ExprStructOfCreator.class,Boolean.class,ExpressionType.PROPERTY,"struct[ure(s| settings)] of %creator%");
		//WorldManagement
		Skript.registerEffect(EffCreateWorld.class, "create world using %creator%");
		Skript.registerEffect(EffUnloadWorld.class, "unload %world% [save %-boolean%]");
		Skript.registerEffect(EffDeleteWorld.class, "delete %world%");
		Skript.registerEffect(EffDuplicateWorld.class, "duplicate %world% using name %string%");
		//TestSyntaxes
		//
		try {
			Field classinfos = Classes.class.getDeclaredField("tempClassInfos");
			classinfos.setAccessible(true);
			@SuppressWarnings("unchecked")
			List<ClassInfo<?>> classes = (List<ClassInfo<?>>) classinfos.get(null);
			for (int i = 0; i < classes.size(); i++)
				registerCustomEventValue(classes.get(i));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		info("Awesome syntaxes have been registered!");
		try {
	        Metrics metrics = new Metrics(this);
	        Graph skriptv = metrics.createGraph("Skript Version");
	        skriptv.addPlotter(new Metrics.Plotter(Bukkit.getServer().getPluginManager().getPlugin("Skript").getDescription().getVersion()){
	        	@Override
	        	public int getValue() {
	        		return 1;
	        	}
	        });
	        Graph addons = metrics.createGraph("Skript Addons");
	        Object[] addonlist = Skript.getAddons().toArray();
	        for (int i = 0; i < addonlist.length; i++) {
	        	addons.addPlotter(new Metrics.Plotter(((SkriptAddon) addonlist[i]).getName()) {
					
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
	        Mundo.reportException(this, e);
	    }
	}
	
	public static void reportException(Object o, Exception e) {
		info("An exception has occured within MundoSK");
		info("Please report this to the MundoSK thread on forums.skunity.com");
		info("Exception at " + o.getClass());
		e.printStackTrace();
	}
	
	public static void info(String s) {
		instance.getLogger().info(s);
	}
	
	public static void debug(Object obj, String msg) {
		if (config.getBoolean("debug_mode")) {
			info(obj.getClass() + ": " + msg);
		}
	}
	
	public static void classDebug(Class<?> cla, String msg) {
		if (config.getBoolean("debug_mode")) {
			info(cla + ": " + msg);
		}
	}
	
	public static <T> void registerCustomEventValue(ClassInfo<T> type) {
		EventValues.registerEventValue(UtilCustomEvent.class, type.getC(), new Getter<T, UtilCustomEvent>() {
			@SuppressWarnings("unchecked")
			@Override
			public T get(UtilCustomEvent e) {
				return (T) e.getDetail(type);
			}
		}, 0);
	}
	
}
