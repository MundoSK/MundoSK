package com.pie.tlatoani.TestSyntaxes;

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
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Tlatoani on 7/10/16.
 */
public class TestTabUpdate extends Effect {
    private Expression<Player> playerExpression;
    private Expression<String> stringExpression;
    private Expression<Number> pingExpression;

    @Override
    protected void execute(Event event) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        Player player = playerExpression.getSingle(event);
        String displayName = stringExpression.getSingle(event);
        WrappedGameProfile gameProfile = new WrappedGameProfile(player.getUniqueId(), displayName);
        WrappedChatComponent chatComponent = WrappedChatComponent.fromText(displayName);
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, pingExpression.getSingle(event).intValue(), EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
        playerInfoDataList.add(playerInfoData);
        packet.getPlayerInfoDataLists().writeSafely(0, playerInfoDataList);
        packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
    }

    @Override
    public String toString(Event event, boolean b) {
        return "mundosk test update_player_info target %player% display_name %string% ping %number%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        stringExpression = (Expression<String>) expressions[1];
        pingExpression = (Expression<Number>) expressions[2];
        return true;
    }
}
