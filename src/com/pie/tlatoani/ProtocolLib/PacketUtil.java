package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Core.Static.Logging;
import mundosk_libraries.packetwrapper.WrapperPlayServerPlayerInfo;
import mundosk_libraries.packetwrapper.WrapperPlayServerScoreboardScore;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Tlatoani on 8/14/17.
 */
public class PacketUtil {

    public static PacketContainer playerInfoPacket(
            String displayName,
            Integer latencyBars,
            GameMode gameMode,
            String name,
            UUID uuid,
            Skin skin,
            EnumWrappers.PlayerInfoAction action
    ) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (skin == null) {
                skin = Tablist.DEFAULT_SKIN_TEXTURE;
            }
            profile.getProperties().put(Skin.MULTIMAP_KEY, skin.toWrappedSignedProperty());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                Optional.ofNullable(latencyBars).map(PacketUtil::getPossibleLatency).orElse(0),
                Optional.ofNullable(gameMode).map(EnumWrappers.NativeGameMode::fromBukkit).orElse(EnumWrappers.NativeGameMode.NOT_SET),
                WrappedChatComponent.fromText(Optional.ofNullable(displayName).orElse(""))
        );
        packet.setData(Collections.singletonList(playerInfoData));
        packet.setAction(action);
        return packet.getHandle();
    }

    public static PacketContainer playerInfoPacket(Player player, EnumWrappers.PlayerInfoAction action) {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        PlayerInfoData playerInfoData = new PlayerInfoData(
                WrappedGameProfile.fromPlayer(player),
                5,
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                null
        );
        packet.setData(Collections.singletonList(playerInfoData));
        packet.setAction(action);
        return packet.getHandle();
    }

    public static PacketContainer scorePacket(String scoreName, String objectiveName, Integer score, EnumWrappers.ScoreboardAction action) {
        WrapperPlayServerScoreboardScore packet = new WrapperPlayServerScoreboardScore();
        packet.setScoreName(scoreName);
        packet.setObjectiveName(objectiveName);
        packet.setValue(Optional.ofNullable(score).orElse(0));
        packet.setScoreboardAction(action);
        return packet.getHandle();
    }

    public static WrappedChatComponent stringsToChatComponent(List<String> strings) {
        if (strings.isEmpty()) {
            return WrappedChatComponent.fromText("");
        }
        StringJoiner joiner = new StringJoiner(", {\"text\":\"\n\"}, ", "{\"extra\":[", "],\"text\":\"\"}");
        for (String string : strings) {
            joiner.add(WrappedChatComponent.fromText(string).getJson());
        }
        Logging.debug(PacketUtil.class, "Final JSON: " + joiner.toString());
        return WrappedChatComponent.fromJson(joiner.toString());
    }

    public static int getPossibleLatency(int latencyBars) {
        switch (latencyBars) {
            case 0: return -1;
            case 1: return 1024;
            case 2: return 768;
            case 3: return 512;
            case 4: return 256;
            case 5: return 0;
            default: throw new IllegalArgumentException("Illegal amount of latency bars: " + latencyBars + ", required 0 <= latency <= 5");
        }
    }
}
