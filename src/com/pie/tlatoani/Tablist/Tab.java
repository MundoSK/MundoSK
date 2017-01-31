package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 1/20/17.
 */
public class Tab {
    public final Tablist tablist;
    public final String name;
    public final UUID uuid;
    protected HashMap<Player, String> displayNames = new HashMap<>();
    protected HashMap<Player, Byte> latencies = new HashMap<>();
    protected HashMap<Player, Skin> icons = new HashMap<>();
    protected HashMap<Player, Integer> scores = new HashMap<>();

    public Tab(Tablist tablist, String name, UUID uuid, String displayName, Byte latency, Skin icon, Integer score) {
        this.tablist = tablist;
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

    private void sendPacket(Player player, EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
        PacketContainer packet = Tablist.playerInfoPacket(displayName, latency == null ? null : latency.intValue(), null, name, uuid, icon, action);
        if (player != null) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                Mundo.reportException(this, e);
            }
        } else {
            tablist.players.forEach(new Consumer<Player>() {
                @Override
                public void accept(Player player1) {
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player1, packet);
                    } catch (InvocationTargetException e) {
                        Mundo.reportException(this, e);
                    }
                }
            });
        }
    }

    protected void updateScore(Player player, Integer score) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().writeSafely(0, name);
        packet.getStrings().writeSafely(1, Tablist.OBJECTIVE_NAME);
        packet.getIntegers().writeSafely(0, Mundo.firstNotNull(score, 0));
        packet.getScoreboardActions().writeSafely(0, EnumWrappers.ScoreboardAction.CHANGE);
        try {
            if (player != null) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } else {
                for (Player player1 : tablist.players) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player1, packet);
                }
            }
        } catch (InvocationTargetException e) {
            Mundo.reportException(Tablist.class, e);
        }
    }

    public void add(Player player) {
        sendPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER, Mundo.getOrDefault(displayNames, player), Mundo.getOrDefault(latencies, player), Mundo.getOrDefault(icons, player));
        if (tablist.areScoresEnabled()) {
            Integer score = Mundo.getOrDefault(scores, player);
            if (score != null) {
                updateScore(player, score);
            }
        }
    }

    public void remove(Player player) {
        sendPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
        if (tablist.areScoresEnabled()) {
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
        Mundo.setInMap(displayNames, player, value);
        sendPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, value, null, null);
    }

    public void setLatency(Player player, Byte value) {
        Mundo.setInMap(latencies, player, value);
        sendPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, value, null);
    }

    public void setIcon(Player player, Skin value) {
        Mundo.setInMap(icons, player, value);
        remove(player);
        Mundo.syncDelay(1, () -> add(player));
    }

    public void setScore(Player player, Integer value) {
        Mundo.setInMap(scores, player, value);
        if (tablist.areScoresEnabled()) {
            updateScore(player, Mundo.firstNotNull(value, 0));
        }
    }

    public static class VariablyVisible extends Tab {

        public VariablyVisible(Tablist tablist, String name, UUID uuid, String displayName, Byte latency, Skin icon, Integer score) {
            super(tablist, name, uuid, displayName, latency, icon, score);
        }

        public boolean visibleForAnyone() {
            return !icons.isEmpty();
        }

        public boolean visibleFor(Player player) {
            return Mundo.getOrDefault(icons, player) != null;
        }

        public void showFor(Player player, String displayName, Byte latency, Skin icon, Integer score) {
            if (!visibleFor(player) || icon != null) {
                Mundo.setInMap(displayNames, player, displayName);
                Mundo.setInMap(latencies, player, latency);
                Mundo.setInMap(icons, player, Mundo.firstNotNull(icon, Tablist.DEFAULT_SKIN_TEXTURE));
                if (player != null ? visibleFor(player) : visibleForAnyone()) {
                    remove(player);
                }
                add(player);
                if (tablist.areScoresEnabled()) {
                    setScore(player, score);
                }
            }
        }

        public void hideFor(Player player) {
            Mundo.setInMap(displayNames, player, null);
            Mundo.setInMap(latencies, player, null);
            Mundo.setInMap(icons, player, null);
            Mundo.setInMap(scores, player, null);
            remove(player);
        }
    }

}
