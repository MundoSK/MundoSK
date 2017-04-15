package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Util.DefaultHashMap;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/**
 * Created by Tlatoani on 1/20/17.
 */
public class OldTab {
    public final OldTablist oldTablist;
    public final String name;
    public final UUID uuid;
    //DefaultHashMap are used for
    protected DefaultHashMap<Player, String> displayNames = new DefaultHashMap<>();
    protected DefaultHashMap<Player, Byte> latencies = new DefaultHashMap<>();
    protected DefaultHashMap<Player, Skin> icons = new DefaultHashMap<>();
    protected DefaultHashMap<Player, Integer> scores = new DefaultHashMap<>();

    public OldTab(OldTablist oldTablist, String name, UUID uuid, String displayName, Byte latency, Skin icon, Integer score) {
        this.oldTablist = oldTablist;
        this.name = name;
        this.uuid = uuid;
        if (displayName != null) {
            displayNames.put(null, displayName);
        }
        if (latency != null) {
            latencies.put(null, latency);
        }
        if (icon != null) {
            icons.put(null, icon);
        }
        if (score != null) {
            scores.put(null, score);
        }
    }

    protected void sendPacket(Player target, EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
        PacketContainer packet = OldTablist.playerInfoPacket(displayName, latency == null ? null : latency.intValue(), null, name, uuid, icon, action);
        if (target != null) {
            UtilPacketEvent.sendPacket(packet, this, target);
        } else {
            UtilPacketEvent.sendPacket(packet, this, oldTablist.players.toArray(new Player[0]));
        }
    }

    protected void updateScore(Player player, Integer score) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().writeSafely(0, name);
        packet.getStrings().writeSafely(1, OldTablist.OBJECTIVE_NAME);
        packet.getIntegers().writeSafely(0, Mundo.firstNotNull(score, 0));
        packet.getScoreboardActions().writeSafely(0, EnumWrappers.ScoreboardAction.CHANGE);
        try {
            if (player != null) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } else {
                for (Player player1 : oldTablist.players) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player1, packet);
                }
            }
        } catch (InvocationTargetException e) {
            Mundo.reportException(OldTablist.class, e);
        }
    }

    public void add(Player player) {
        sendPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER, displayNames.getOrDefault(player), latencies.getOrDefault(player), icons.getOrDefault(player));
        if (oldTablist.areScoresEnabled()) {
            Integer score = scores.getOrDefault(player);
            if (score != null) {
                updateScore(player, score);
            }
        }
    }

    public void remove(Player player) {
        sendPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
        if (oldTablist.areScoresEnabled()) {
            updateScore(player, null);
        }
    }

    public String getDisplayName(Player player) {
        return displayNames.get(player);
    }

    public Byte getLatency(Player player) {
        return latencies.get(player);
    }

    public Skin getIcon(Player player) {
        return icons.get(player);
    }

    public Integer getScore(Player player) {
        return scores.get(player);
    }

    public void setDisplayName(Player player, String value) {
        displayNames.put(player, value);
        sendPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, value, null, null);
    }

    public void setLatency(Player player, Byte value) {
        latencies.put(player, value);
        sendPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, value, null);
    }

    public void setIcon(Player player, Skin value) {
        icons.put(player, value);
        remove(player);
        Mundo.sync(1, () -> add(player));
    }

    public void setScore(Player player, Integer value) {
        scores.put(player, value);
        if (oldTablist.areScoresEnabled()) {
            updateScore(player, Mundo.firstNotNull(value, 0));
        }
    }

    public static class VariablyVisible extends OldTab {
        protected DefaultHashMap<Player, Boolean> visibility = new DefaultHashMap<>();

        public VariablyVisible(OldTablist oldTablist, String name, UUID uuid, String displayName, Byte latency, Skin icon, Integer score, boolean initialVisibility) {
            super(oldTablist, name, uuid, displayName, latency, icon, score);
            visibility.put(null, initialVisibility);
        }

        public boolean visibleForAnyone() {
            return visibility.containsValue(true);
        }

        public boolean visibleFor(Player player) {
            return visibility.getOrDefault(player);
        }

        public void showFor(Player player, String displayName, Byte latency, Skin icon, Integer score) {
            if (!visibleFor(player) || icon != null) {
                displayNames.put(player, displayName);
                latencies.put(player, latency);
                icons.put(player, icon);
                scores.put(player, score);
                visibility.put(player, true);
                if (player != null ? visibleFor(player) : visibleForAnyone()) {
                    remove(player);
                }
                add(player);
                if (oldTablist.areScoresEnabled()) {
                    setScore(player, score);
                }
            }
        }

        public void hideFor(Player player) {
            displayNames.put(player, null);
            latencies.put(player, null);
            icons.put(player, null);
            scores.put(player, null);
            visibility.put(player, false);
            remove(player);
        }

        @Override
        public void setIcon(Player player, Skin value) {
            if (visibleFor(player)) {
                super.setIcon(player, value);
            }
        }
    }

}
