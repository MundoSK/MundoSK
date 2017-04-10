package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 4/9/17.
 */
public class Tab {
    public final Tablist tablist;
    public final String name;
    public final UUID uuid;

    protected String displayName = null;
    protected Byte latency = null;
    protected Skin icon = null;
    protected Integer score = null;

    public Tab(Tablist tablist, String name, UUID uuid) {
        this.tablist = tablist;
        this.name = name;
        this.uuid = uuid;
    }

    protected void sendPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
        PacketContainer packet = Tablist.playerInfoPacket(displayName, latency == null ? null : latency.intValue(), null, name, uuid, icon, action);
        UtilPacketEvent.sendPacket(packet, this, tablist.players);
    }

    protected void sendPacket(Player target, EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
        PacketContainer packet = Tablist.playerInfoPacket(displayName, latency == null ? null : latency.intValue(), null, name, uuid, icon, action);
        UtilPacketEvent.sendPacket(packet, this, target);
    }

    protected void updateScore(Integer score) {
        PacketContainer packet = Tablist.scorePacket(name, Tablist.OBJECTIVE_NAME, score, EnumWrappers.ScoreboardAction.CHANGE);
        UtilPacketEvent.sendPacket(packet, this, tablist.players);
    }

    protected void updateScore(Player player, Integer score) {
        PacketContainer packet = Tablist.scorePacket(name, Tablist.OBJECTIVE_NAME, score, EnumWrappers.ScoreboardAction.CHANGE);
        UtilPacketEvent.sendPacket(packet, this, player);
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
        sendPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, value, null, null);
    }

    public void setLatency(Byte value) {
        latency = value;
        sendPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, value, null);
    }

    public void setIcon(Skin value) {
        icon = value;
        sendPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
        Mundo.sync(1, () -> sendPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, displayName, latency, icon));
    }

    public void setScore(Integer value) {
        score = value;
        if (tablist.areScoresEnabled()) {
            updateScore(value);
        }
    }

    public static class Personalizable extends Tab {
        public HashMap<Player, Optional<Personal>> personalTabs = new HashMap<>();
        public HashSet<Player> blindPlayers = new HashSet<>();

        public Personalizable(Tablist tablist, String name, UUID uuid) {
            super(tablist, name, uuid);
        }

        public Personal forPlayer(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional == null) {
                return new Personal(this, player);
            } else {
                return personalOptional.orElse(null);
            }
        }

        public boolean visibleFor(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            return personalOptional == null || personalOptional.isPresent();
        }

        public void showFor(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (!personalOptional.isPresent()) {
                personalTabs.remove(player);
                sendPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER, displayName, latency, icon);
            }
        }

        public void hideFor(Player player) {
            sendPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
            personalTabs.put(player, Optional.empty());
        }

        public boolean isUniform() {
            return personalTabs.isEmpty() && blindPlayers.isEmpty();
        }

        public void setDisplayName(String value) {
            super.setDisplayName(value);
            for (Optional<Personal> personalOptional : personalTabs.values()) {
                personalOptional.ifPresent(tab -> {
                    tab.displayName = null;
                    tab.checkStored();
                });
            }
        }

        public void setLatency(Byte value) {
            super.setLatency(value);
            for (Optional<Personal> personalOptional : personalTabs.values()) {
                personalOptional.ifPresent(tab -> {
                    tab.latency = null;
                    tab.checkStored();
                });
            }
        }

        public void setIcon(Skin value) {
            icon = value;
            sendPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
            Mundo.sync(1, () -> {
                for (Player player : tablist.players) {
                    Optional<Personal> personalOptional = personalTabs.get(player);
                    if (personalOptional == null) {
                        sendPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER, displayName, latency, icon);
                    } else {
                        personalOptional.ifPresent(tab -> sendPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER,
                                tab.getDisplayName(),
                                tab.getLatency(),
                                tab.getIcon()
                        ));
                    }
                }
            });
        }

        public void setScore(Integer value) {
            super.setScore(value);
            for (Optional<Personal> personalOptional : personalTabs.values()) {
                personalOptional.ifPresent(tab -> {
                    tab.score = null;
                    tab.checkStored();
                });
            }
        }
    }

    public static class Personal extends Tab {
        public final Player player;
        public final Personalizable parent;
        private boolean stored = false;

        public Personal(Personalizable parent, Player player) {
            super(parent.tablist, parent.name, parent.uuid);
            this.parent = parent;
            this.player = player;
        }

        private void checkStored() {
            if (stored) {
                if (displayName == null && latency == null && icon == null && score == null) {
                    stored = false;
                    parent.personalTabs.remove(player);
                }
            } else {
                if (displayName != null || latency != null || icon != null || score != null) {
                    stored = true;
                    parent.personalTabs.put(player, Optional.of(this));
                }
            }
        }

        @Override
        protected void sendPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
            sendPacket(player, action, displayName, latency, icon);
        }

        @Override
        protected void updateScore(Integer score) {
            updateScore(player, score);
        }

        @Override
        public String getDisplayName() {
            return displayName != null ? displayName : parent.displayName;
        }

        @Override
        public Byte getLatency() {
            return latency != null ? latency : parent.latency;
        }

        @Override
        public Skin getIcon() {
            return icon != null ? icon : parent.icon;
        }

        @Override
        public Integer getScore() {
            return score != null ? score : parent.score;
        }

        @Override
        public void setDisplayName(String value) {
            displayName = value;
            if (value == null) {
                value = parent.displayName;
            }
            sendPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, value, null, null);
            checkStored();
        }

        @Override
        public void setLatency(Byte value) {
            latency = value;
            if (value == null) {
                value = parent.latency;
            }
            sendPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, value, null);
            checkStored();
        }

        @Override
        public void setIcon(Skin value) {
            icon = value;
            sendPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
            Mundo.sync(1, () -> sendPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, displayName, latency, icon));
            checkStored();
        }

        @Override
        public void setScore(Integer value) {
            score = value;
            if (value == null) {
                value = parent.score;
            }
            if (tablist.areScoresEnabled()) {
                updateScore(value);
            }
            checkStored();
        }
    }
}
