package com.pie.tlatoani.Tablist.Tab;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Tlatoani on 4/26/17.
 */
public class InvisibleByDefaultTab implements PersonalizableTab {
    protected HashMap<Player, PersonalTab> personalTabs = new HashMap<>();
    protected final Tablist.Storage storage;

    public final String name;
    public final UUID uuid;

    public InvisibleByDefaultTab(Tablist.Storage storage, String name, UUID uuid) {
        this.storage = storage;
        this.name = name;
        this.uuid = uuid;
    }

    public Map<Player, PersonalTab> view() {
        return Collections.unmodifiableMap(personalTabs);
    }

    @Override
    public Optional<? extends Tab> viewFor(Player player) {
        return Optional.ofNullable(personalTabs.get(player));
    }

    @Override
    public PersonalTab forceFor(Player player) {
        return personalTabs.get(player);
    }

    @Override
    public boolean visibleFor(Player player) {
        return personalTabs.containsKey(player);
    }

    @Override
    public void showFor(Player player) {
        personalTabs.computeIfAbsent(player, __ -> {
            PersonalTab personalTab = new PersonalTab(this, player);
            personalTab.send(showPacket());
            return personalTab;
        });
    }

    @Override
    public PersonalTab showFor(Player player, String displayName, Byte latency, Skin icon) {
        return personalTabs.compute(player, (__, personalTab) -> {
            if (personalTab == null) {
                personalTab = new PersonalTab(this, player);
                personalTab.displayName = Optional.ofNullable(displayName);
                personalTab.latency = Optional.ofNullable(latency);
                personalTab.icon = Optional.ofNullable(icon);
                personalTab.send(personalTab.showPacket());
            } else {
                if (icon == null && personalTab.icon == null) {
                    personalTab.setDisplayName(displayName);
                    personalTab.setLatency(latency);
                } else {
                    personalTab.displayName = Optional.ofNullable(displayName);
                    personalTab.latency = Optional.of(latency);
                    personalTab.setIcon(icon);
                }
            }
            return personalTab;
        });
    }

    @Override
    public void hideFor(Player player) {
        PersonalTab personalTab = personalTabs.remove(player);
        if (personalTab != null) {
            personalTab.send(hidePacket());
        }
    }

    @Override
    public Tablist getTablist() {
        return storage.tablist;
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

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public Byte getLatency() {
        return null;
    }

    @Override
    public Skin getIcon() {
        return null;
    }

    @Override
    public Integer getScore() {
        return null;
    }

    @Override
    public void setDisplayName(String value) {}

    @Override
    public void setLatency(Byte value) {}

    @Override
    public void setIcon(Skin value) {}

    @Override
    public void setScore(Integer value) {}
}
