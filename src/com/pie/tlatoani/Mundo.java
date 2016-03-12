package com.pie.tlatoani;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.pie.tlatoani.Achievement.CondHasAch;
import com.pie.tlatoani.Achievement.EffAwardAch;
import com.pie.tlatoani.Achievement.EffRemoveAch;
import com.pie.tlatoani.Achievement.ExprParentAch;
import com.pie.tlatoani.Book.ExprAuthorOfBook;
import com.pie.tlatoani.Book.ExprBook;
import com.pie.tlatoani.Book.ExprPageCountOfBook;
import com.pie.tlatoani.Book.ExprPageOfBook;
import com.pie.tlatoani.Book.ExprPagesOfBook;
import com.pie.tlatoani.Book.ExprTitleOfBook;
import com.pie.tlatoani.EnchantedBook.ExprEnchBookWithEnch;
import com.pie.tlatoani.EnchantedBook.ExprEnchantLevelInEnchBook;
import com.pie.tlatoani.EnchantedBook.ExprEnchantsInEnchBook;
import com.pie.tlatoani.Misc.ExprDifficulty;
import com.pie.tlatoani.Misc.ExprGameRule;
import com.pie.tlatoani.Misc.ExprHighestSolidBlock;
import com.pie.tlatoani.Misc.ExprWorldString;
import com.pie.tlatoani.Socket.CondFunctionSocketIsOpen;
import com.pie.tlatoani.Socket.CondServerSocketIsOpen;
import com.pie.tlatoani.Socket.EffCloseFunctionSocket;
import com.pie.tlatoani.Socket.EffOpenFunctionSocket;
import com.pie.tlatoani.Socket.EffWriteToSocket;
import com.pie.tlatoani.Socket.ExprHandlerOfFunctionSocket;
import com.pie.tlatoani.Socket.ExprPassOfFunctionSocket;
import com.pie.tlatoani.TerrainControl.CondWorld;
import com.pie.tlatoani.TerrainControl.EffSpawnObject;
import com.pie.tlatoani.TerrainControl.ExprBiomeAt;
import com.pie.tlatoani.WorldBorder.CondBeyondBorder;
import com.pie.tlatoani.WorldBorder.CondWithinBorder;
import com.pie.tlatoani.WorldBorder.EffResetBorder;
import com.pie.tlatoani.WorldBorder.EvtBorderStabilize;
import com.pie.tlatoani.WorldBorder.ExprCenterOfBorder;
import com.pie.tlatoani.WorldBorder.ExprDamageAmountOfBorder;
import com.pie.tlatoani.WorldBorder.ExprDamageBufferOfBorder;
import com.pie.tlatoani.WorldBorder.ExprFinalSizeOfBorder;
import com.pie.tlatoani.WorldBorder.ExprSizeOfBorder;
import com.pie.tlatoani.WorldBorder.ExprTimeRemainingUntilBorderStabilize;
import com.pie.tlatoani.WorldBorder.ExprWarningDistanceOfBorder;
import com.pie.tlatoani.WorldBorder.ExprWarningTimeOfBorder;
import com.pie.tlatoani.WorldBorder.UtilBorderStabilize;
import com.pie.tlatoani.WorldCreator.ExprCreatorNamed;
import com.pie.tlatoani.WorldCreator.ExprCreatorOf;
import com.pie.tlatoani.WorldCreator.ExprCreatorOfWith;
import com.pie.tlatoani.WorldCreator.ExprEnvOfCreator;
import com.pie.tlatoani.WorldCreator.ExprGenOfCreator;
import com.pie.tlatoani.WorldCreator.ExprGenSettingsOfCreator;
import com.pie.tlatoani.WorldCreator.ExprNameOfCreator;
import com.pie.tlatoani.WorldCreator.ExprSeedOfCreator;
import com.pie.tlatoani.WorldCreator.ExprStructOfCreator;
import com.pie.tlatoani.WorldCreator.ExprTypeOfCreator;
import com.pie.tlatoani.WorldManagement.EffCreateWorld;
import com.pie.tlatoani.WorldManagement.EffDeleteWorld;
import com.pie.tlatoani.WorldManagement.EffDuplicateWorld;
import com.pie.tlatoani.WorldManagement.EffUnloadWorld;

import ch.njol.skript.Skript;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void onEnable(){
		instance = this;
		Skript.registerAddon(this);
		this.getLogger().info("Pie is awesome :D");
		Classes.registerClass(new ClassInfo<WorldCreator>(WorldCreator.class, "creator").user(new String[]{"creator"}).name("creator").parser(new Parser<WorldCreator>(){

            public WorldCreator parse(String s, ParseContext context) {
                return null;
            }

            public String toString(WorldCreator creator, int flags) {
                return null;
            }

            public String toVariableNameString(WorldCreator creator) {
                return null;
            }

            public String getVariableNamePattern() {
                return ".+";
            }
        }));
		Skript.registerExpression(ExprWorldString.class,World.class,ExpressionType.PROPERTY,"world %string%");
		Skript.registerExpression(ExprSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"size of %world% [over %-timespan%]");
		Skript.registerExpression(ExprCenterOfBorder.class,Location.class,ExpressionType.PROPERTY,"center of %world%");
		Skript.registerExpression(ExprDamageAmountOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage amount of %world%");
		Skript.registerExpression(ExprDamageBufferOfBorder.class,Double.class,ExpressionType.PROPERTY,"damage buffer of %world%");
		Skript.registerExpression(ExprWarningDistanceOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning distance of %world%");
		Skript.registerExpression(ExprWarningTimeOfBorder.class,Integer.class,ExpressionType.PROPERTY,"warning time of %world%");
		Skript.registerExpression(ExprFinalSizeOfBorder.class,Double.class,ExpressionType.PROPERTY,"final size of %world%");
		Skript.registerExpression(ExprTimeRemainingUntilBorderStabilize.class,Timespan.class,ExpressionType.PROPERTY,"time remaining until border stabilize in %world%");
		Skript.registerExpression(ExprCreatorNamed.class,WorldCreator.class,ExpressionType.PROPERTY,"creator (with name|named) %string%");
		Skript.registerExpression(ExprCreatorOfWith.class,WorldCreator.class,ExpressionType.PROPERTY,"%creator%[ modified],[ name %-string%][,][ env[ironment] %-string%][,][ seed %-string%][,][ type %-string%][,][ gen[erator] %-string%][,][ gen[erator] settings %-string%][,][ struct[ures] %-boolean%]");
		Skript.registerExpression(ExprCreatorOf.class,WorldCreator.class,ExpressionType.PROPERTY,"creator of %world%");
		Skript.registerExpression(ExprGameRule.class,String.class,ExpressionType.PROPERTY,"value of [game]rule %string% in %world%");
		Skript.registerExpression(ExprNameOfCreator.class,String.class,ExpressionType.PROPERTY,"worldname of %creator%");
		Skript.registerExpression(ExprEnvOfCreator.class,String.class,ExpressionType.PROPERTY,"env[ironment] of %creator%");
		Skript.registerExpression(ExprSeedOfCreator.class,String.class,ExpressionType.PROPERTY,"seed of %creator%");
		Skript.registerExpression(ExprGenOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] of %creator%");
		Skript.registerExpression(ExprGenSettingsOfCreator.class,String.class,ExpressionType.PROPERTY,"gen[erator] set[tings] of %creator%");
		Skript.registerExpression(ExprTypeOfCreator.class,String.class,ExpressionType.PROPERTY,"type of %creator%");
		Skript.registerExpression(ExprStructOfCreator.class,Boolean.class,ExpressionType.PROPERTY,"struct[ure(s| settings)] of %creator%");
		Skript.registerExpression(ExprHighestSolidBlock.class,Block.class,ExpressionType.PROPERTY,"highest [(solid|non-air)] block at %location%");
		Skript.registerExpression(ExprBook.class,ItemStack.class,ExpressionType.PROPERTY,"%itemstack% titled %-string%, [written] by %-string%, [with] %number% page[s] [%-strings%]");
		Skript.registerExpression(ExprTitleOfBook.class,String.class,ExpressionType.PROPERTY,"title of %itemstack%");
		Skript.registerExpression(ExprAuthorOfBook.class,String.class,ExpressionType.PROPERTY,"author of %itemstack%");
		Skript.registerExpression(ExprPageOfBook.class,String.class,ExpressionType.PROPERTY,"(page|pg) %number% of %itemstack%");
		Skript.registerExpression(ExprPagesOfBook.class,String.class,ExpressionType.PROPERTY,"(pages|pgs) [from %-number%] [to %-number%] of %itemstack%");
		Skript.registerExpression(ExprPageCountOfBook.class,Integer.class,ExpressionType.PROPERTY,"page count of %itemstack%");
		Skript.registerExpression(ExprEnchBookWithEnch.class,ItemStack.class,ExpressionType.PROPERTY,"%itemstack% containing %enchantmenttypes%");
		Skript.registerExpression(ExprEnchantLevelInEnchBook.class,Integer.class,ExpressionType.PROPERTY,"level of %enchantmenttype% within %itemstack%");
		Skript.registerExpression(ExprEnchantsInEnchBook.class,EnchantmentType.class,ExpressionType.PROPERTY,"enchants within %itemstack%");
		Skript.registerExpression(ExprDifficulty.class,String.class,ExpressionType.PROPERTY,"difficulty of %world%");
		Skript.registerExpression(ExprParentAch.class,String.class,ExpressionType.PROPERTY,"parent of achieve[ment] %string%");
		Skript.registerExpression(ExprPassOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"pass[word] of function socket at port %number%");
		Skript.registerExpression(ExprHandlerOfFunctionSocket.class,String.class,ExpressionType.PROPERTY,"handler [function] of function socket at port %number%");
		Skript.registerEffect(EffResetBorder.class, "reset %world%");
		Skript.registerEffect(EffCreateWorld.class, "create world using %creator%");
		Skript.registerEffect(EffUnloadWorld.class, "unload %world% [save %-boolean%]");
		Skript.registerEffect(EffDeleteWorld.class, "delete %world%");
		Skript.registerEffect(EffDuplicateWorld.class, "duplicate %world% using name %string%");
		Skript.registerEffect(EffAwardAch.class, "award achieve[ment] %string% to %player%");
		Skript.registerEffect(EffRemoveAch.class, "remove achieve[ment] %string% from %player%");
		Skript.registerEffect(EffWriteToSocket.class, "write %strings% to socket with host %string% port %number% [with timeout %-timespan%] [to handle response through function %-string% with id %-string%]");
		Skript.registerEffect(EffOpenFunctionSocket.class, "open function socket at port %number% [with password %-string%] [through function %-string%]");
		Skript.registerEffect(EffCloseFunctionSocket.class, "close function socket at port %number%");
		Skript.registerCondition((Class)CondHasAch.class, (String[])new String[]{"%player% has achieve[ment] %string%"});
		Skript.registerCondition((Class)CondBeyondBorder.class, (String[])new String[]{"%location% is beyond border"});
		Skript.registerCondition((Class)CondWithinBorder.class, (String[])new String[]{"%location% is within border"});
		Skript.registerCondition((Class)CondFunctionSocketIsOpen.class, (String[])new String[]{"function socket is open at port %number%"});
		Skript.registerCondition((Class)CondServerSocketIsOpen.class, (String[])new String[]{"server socket is open at host %string% port %number% [with timeout of %-timespan%]"});
		Skript.registerEvent("Border Stabilize", EvtBorderStabilize.class, UtilBorderStabilize.class, "border stabilize [in %world%]");
		EventValues.registerEventValue(UtilBorderStabilize.class, World.class, new Getter<World, UtilBorderStabilize>() {
			@Override
			public World get(UtilBorderStabilize e) {
				return e.getWorld();
			}
		}, 0);
		if (Bukkit.getServer().getPluginManager().getPlugin("TerrainControl") != null) {
			this.getLogger().info("You uncovered the secret TerrainControl syntaxes!");
			Skript.registerExpression(ExprBiomeAt.class,String.class,ExpressionType.PROPERTY,"(tc|terrain control) biome at %location%");
			Skript.registerEffect(EffSpawnObject.class, "(tc|terrain control) spawn %string% at %location% with rotation %string%");
			Skript.registerCondition((Class)CondWorld.class, (String[])new String[]{"(tc|terrain control) is enabled for %world%"});
		}
		this.getLogger().info("Awesome syntaxes have been registered!");
		try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	        Mundo.reportException(this, e);
	    }
	}
	
	public static void reportException(Object o, Exception e) {
		instance.getLogger().info("Exception at " + o.getClass());
		e.printStackTrace();
	}
	
}
