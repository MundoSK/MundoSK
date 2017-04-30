package com.pie.tlatoani.Tablist.Tab;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Tlatoani on 4/26/17.
 */
public class BaseTab implements Tab {
    public final String name;
    public final UUID uuid;

    protected final Tablist.Storage storage;

    protected String displayName;
    protected Byte latency;
    protected Skin icon;
    protected Integer score;

    public BaseTab(Tablist.Storage storage, String name, UUID uuid, String displayName, Byte latency, Skin icon, Integer score) {
        this.storage = storage;
        this.name = name;
        this.uuid = uuid;
        this.displayName = displayName;
        this.latency = latency;
        this.icon = icon;
        this.score = score;
    }

    public BaseTab(BaseTab prev) {
        this.storage = prev.storage;
        this.name = prev.name;
        this.uuid = prev.uuid;
        this.displayName = prev.displayName;
        this.latency = prev.latency;
        this.icon = prev.icon;
        this.score = prev.score;
    }

    public Tablist getTablist() {
        return storage.tablist;
    }

    @Override
    public void addPlayer(Player player) {
        send(showPacket(), player);
    }

    @Override
    public void removePlayer(Player player) {
        send(hidePacket(), player);
    }

    public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
        return TablistManager.playerInfoPacket(displayName, latency == null ? null : latency.intValue(), null, name, uuid, icon, action);
    }

    public PacketContainer updateScorePacket(Integer score) {
        return TablistManager.scorePacket(name, Tablist.OBJECTIVE_NAME, score, EnumWrappers.ScoreboardAction.CHANGE);
    }

    public void send(PacketContainer packet) {
        UtilPacketEvent.sendPacket(packet, this, storage.players);
    }

    public void send(PacketContainer packet, Player to) {
        UtilPacketEvent.sendPacket(packet, this, to);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Byte getLatency() {
        return latency;
    }

    public Skin getIcon() {
        return icon;
    }

    public Integer getScore() {
        return score;
    }

    public void setDisplayName(String value) {
        displayName = value;
        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, value, null, null));
    }

    public void setLatency(Byte value) {
        latency = value;
        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, value, null));
    }

    public void setIcon(Skin value) {
        icon = value;
        send(hidePacket());
        Mundo.sync(1, () -> send(showPacket()));
    }

    public void setScore(Integer value) {
        score = value;
        if (getTablist().areScoresEnabled()) {
            send(updateScorePacket(value));
        }
    }
}
