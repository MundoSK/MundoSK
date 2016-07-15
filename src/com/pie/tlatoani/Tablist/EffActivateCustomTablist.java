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
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffActivateCustomTablist extends Effect {
    private Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        Player player = playerExpression.getSingle(event);
        if (!TabListManager.isActivated(player)) {
            final ArrayList<PlayerInfoData> dataArrayList = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(new Consumer<Player>() {
                @Override
                public void accept(Player player1) {
                    WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player1);
                    PlayerInfoData playerInfoData = new PlayerInfoData(wrappedGameProfile, 5, EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText(""));
                    dataArrayList.add(playerInfoData);
                }
            });
            PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packetContainer.getPlayerInfoDataLists().writeSafely(0, dataArrayList);
            packetContainer.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            try {
                UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
                TabListManager.setActivated(player, true);
            } catch (InvocationTargetException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "activate custom tablist for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }
}
