package com.pie.tlatoani.Tablist;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffSetCustomTablist extends Effect {
    private Expression<Player> playerExpression;
    private int matchedPattern;
    private Expression<Number> columns;
    private Expression<Number> rows;
    private Expression<Object> iconExpression;

    @Override
    protected void execute(Event event) {
        Player player = playerExpression.getSingle(event);
        if (matchedPattern != 3 && !TabListManager.hasCustomTabList(player)) {
            Bukkit.getOnlinePlayers().forEach(new Consumer<Player>() {
                @Override
                public void accept(Player player1) {
                    WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player1);
                    PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 5, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(""));
                    PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packetContainer.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                    packetContainer.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    try {
                        UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
                    } catch (InvocationTargetException e) {
                        Mundo.reportException(this, e);
                    }
                }
            });
        }
        if (matchedPattern == 1) {
            TabListManager.setSimpleTabList(player);
        } else if (matchedPattern == 2) {
            Mundo.debug(this, "matchedPattern == 2");
            int finalColumns = columns == null ? 4 : columns.getSingle(event).intValue();
            int finalRows = rows == null ? 20 : rows.getSingle(event).intValue();
            TabListIcon finalIcon = iconExpression == null ? TabListIcon.alex() : TabListIcon.convertSkriptValueToTabListIcon(iconExpression.getSingle(event));
            TabListManager.setArrayTabList(player, finalColumns, finalRows, finalIcon);
        } else if (TabListManager.hasCustomTabList(player)) {
            TabListManager.clearTabList(player);
            Bukkit.getOnlinePlayers().forEach(new Consumer<Player>() {
                @Override
                public void accept(Player player) {
                    WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
                    PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(TabListManager.colorStringToJson(player.getPlayerListName())));
                    PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packetContainer.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                    packetContainer.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    try {
                        UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
                    } catch (InvocationTargetException e) {
                        Mundo.reportException(this, e);
                    }
                }
            });
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "activate custom tablist for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        matchedPattern = i + 1;
        if (matchedPattern == 2) {
            columns = (Expression<Number>) expressions[1];
            rows = (Expression<Number>) expressions[2];
            iconExpression = (Expression<Object>) expressions[3];
        }
        return true;
    }
}
