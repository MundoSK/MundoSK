package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Tlatoani on 5/8/17.
 */
public class Tab {
    public final Player player;
    public final String name;
    public final UUID uuid;

    private String displayName;
    private Integer latency;
    private Skin icon;
    private Integer score;

    public Tab(Player player, String name, UUID uuid, String displayName, Integer latency, Skin icon, Integer score) {
        this.player = player;
        this.name = name;
        this.uuid = uuid;

        this.displayName = displayName;
        this.latency = latency;
        this.icon = icon;
        this.score = score;
    }

    public void sendPacket(PacketContainer packet) {
        PacketManager.sendPacket(packet, this, player);
    }

    public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action) {
        return PacketUtil.playerInfoPacket(displayName, latency, null, name, uuid, icon, action);
    }

    public PacketContainer updateScorePacket() {
        return PacketUtil.scorePacket(name, Tablist.OBJECTIVE_NAME, score, EnumWrappers.ScoreboardAction.CHANGE);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME));
    }

    public Integer getLatency() {
        return latency;
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
        sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY));
    }

    public Skin getIcon() {
        return icon;
    }

    public void setIcon(Skin icon) {
        sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
        this.icon = icon;
        sendPacket(playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
        sendPacket(updateScorePacket());
    }
}
