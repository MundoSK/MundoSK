package com.pie.tlatoani.TablistNew;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO;

/**
 * Created by Tlatoani on 8/13/17.
 */
public class TablistUtil {

    public static PacketContainer playerInfoPacket(
            String displayName,
            Integer latency,
            GameMode gameMode,
            String name,
            UUID uuid,
            Skin skin,
            EnumWrappers.PlayerInfoAction action
    ) {
        PacketContainer result = new PacketContainer(PLAYER_INFO);
        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (skin == null) {
                skin = Tablist.DEFAULT_SKIN_TEXTURE;
            }
            profile.getProperties().put(Skin.MULTIMAP_KEY, skin.toWrappedSignedProperty());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                Optional.ofNullable(latency).orElse(5),
                Optional.ofNullable(gameMode).map(mode -> EnumWrappers.NativeGameMode.fromBukkit(mode)).orElse(EnumWrappers.NativeGameMode.NOT_SET),
                WrappedChatComponent.fromText(Optional.ofNullable(displayName).orElse(""))
        );
        result.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        result.getPlayerInfoAction().writeSafely(0, action);
        return result;
    }

    public static PacketContainer playerInfoPacket(Player player, EnumWrappers.PlayerInfoAction action) {
        PacketContainer result = new PacketContainer(PLAYER_INFO);
        PlayerInfoData playerInfoData = new PlayerInfoData(
                WrappedGameProfile.fromPlayer(player),
                5,
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                WrappedChatComponent.fromText(player.getPlayerListName())
        );
        result.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        result.getPlayerInfoAction().writeSafely(0, action);
        return result;
    }

    public static PacketContainer scorePacket(String scoreName, String objectiveName, Integer score, EnumWrappers.ScoreboardAction action) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().writeSafely(0, scoreName);
        packet.getStrings().writeSafely(1, objectiveName);
        packet.getIntegers().writeSafely(0, Optional.ofNullable(score).orElse(0));
        packet.getScoreboardActions().writeSafely(0, action);
        return packet;
    }
}
