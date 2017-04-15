package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 4/9/17.
 */
public class Tab {
    public final OldTablist oldTablist;
    public final String name;
    public final UUID uuid;

    protected String displayName;
    protected Byte latency;
    protected Skin icon;
    protected Integer score;

    public Tab(OldTablist oldTablist, String name, UUID uuid, String displayName, Byte latency, Skin icon, Integer score) {
        this.oldTablist = oldTablist;
        this.name = name;
        this.uuid = uuid;
        this.displayName = displayName;
        this.latency = latency;
        this.icon = icon;
        this.score = score;
    }

    public Tab(Tab prev) {
        this.oldTablist = prev.oldTablist;
        this.name = prev.name;
        this.uuid = prev.uuid;
        this.displayName = prev.displayName;
        this.latency = prev.latency;
        this.icon = prev.icon;
        this.score = prev.score;
    }

    public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
        return OldTablist.playerInfoPacket(displayName, latency == null ? null : latency.intValue(), null, name, uuid, icon, action);
    }

    public PacketContainer showPacket() {
        return playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, displayName, latency, icon);
    }

    public PacketContainer hidePacket() {
        return playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
    }

    public PacketContainer updateScorePacket(Integer score) {
        return OldTablist.scorePacket(name, OldTablist.OBJECTIVE_NAME, score, EnumWrappers.ScoreboardAction.CHANGE);
    }

    public void send(PacketContainer packet) {
        UtilPacketEvent.sendPacket(packet, this, oldTablist.players);
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
        if (oldTablist.areScoresEnabled()) {
            send(updateScorePacket(value));
        }
    }

    public boolean containsValue() {
        return displayName != null
                || latency != null
                || icon != null
                || score != null;
    }

    public static class Personalizable extends Tab {
        protected HashMap<Player, Optional<Personal>> personalTabs = new HashMap<>();
        protected boolean visibleByDefault;

        public Personalizable(OldTablist oldTablist, String name, UUID uuid) {
            super(oldTablist, name, uuid, null, null, null, null);
            visibleByDefault = false;
        }

        public Personalizable(Tab prev) {
            super(prev);
            visibleByDefault = true;
        }

        public boolean isUniform() {
            return personalTabs.isEmpty();
        }

        public boolean isVisibleByDefault() {
            return visibleByDefault;
        }

        public void showForAll() {
            visibleByDefault = true;
            send(showPacket());
        }

        public void hideForAll() {
            if (visibleByDefault) {
                visibleByDefault = false;
                personalTabs.clear();
                send(hidePacket());
            } else if (!isUniform()) {
                UtilPacketEvent.sendPacket(hidePacket(), this, personalTabs.keySet());
            }
        }

        public Personal forPlayer(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional == null) {
                return null;
            } else {
                return personalOptional.orElse(null);
            }
        }

        public Personal forceForPlayer(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional == null) {
                Personal personal = new Personal(this, player);
                personalTabs.put(player, Optional.of(personal));
                return personal;
            } else {
                return personalOptional.orElse(null);
            }
        }

        public boolean visibleFor(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional == null) {
                return visibleByDefault;
            }
            return personalOptional.isPresent();
        }

        public void showFor(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional == null) {
                if (!visibleByDefault) {
                    personalTabs.put(player, Optional.of(new Personal(this, player)));
                    send(showPacket(), player);
                }
            } else if (!personalOptional.isPresent()) {
                if (visibleByDefault) {
                    personalTabs.remove(player);
                } else {
                    personalTabs.put(player, Optional.of(new Personal(this, player)));
                }
                send(showPacket(), player);
            }
        }

        public Personal showFor(Player player, String displayName, Byte latency, Skin icon) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional != null && personalOptional.isPresent()) {
                Personal personal = personalOptional.get();
                personal.setIcon(icon);
                return personal;
            } else {
                Personal personal = new Personal(this, player);
                if (personalOptional == null && visibleByDefault) {
                    personal.send(personal.hidePacket());
                }
                personal.displayName = displayName;
                personal.latency = latency;
                personal.icon = icon;
                personal.send(personal.showPacket());
                personalTabs.put(player, Optional.of(personal));
                return personal;
            }
        }

        public void hideFor(Player player) {
            send(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null), player);
            if (visibleByDefault) {
                personalTabs.put(player, Optional.empty());
            } else {
                personalTabs.remove(player);
            }
        }

        public void removePlayer(Player player) {
            Optional<Personal> personalOptional = personalTabs.get(player);
            if (personalOptional == null) {
                if (visibleByDefault) {
                    send(hidePacket(), player);
                }
            } else {
                personalOptional.ifPresent(__ -> send(hidePacket(), player));
                personalTabs.remove(player);
            }
        }

        protected void removeIfApplicable(Personal personal) {
            if (visibleByDefault
                    && personal.displayName == null
                    && personal.latency == null
                    && personal.icon == null
                    && personal.score == null) {
                personalTabs.remove(personal.player);
            }
        }

        public void setDisplayName(String value) {
            if (!visibleByDefault) {
                return;
            }
            super.setDisplayName(value);
            for (Optional<Personal> personalOptional : personalTabs.values()) {
                personalOptional.ifPresent(tab -> {
                    tab.displayName = null;
                    removeIfApplicable(tab);
                });
            }
        }

        public void setLatency(Byte value) {
            if (!visibleByDefault) {
                return;
            }
            super.setLatency(value);
            for (Optional<Personal> personalOptional : personalTabs.values()) {
                personalOptional.ifPresent(tab -> {
                    tab.latency = null;
                    removeIfApplicable(tab);
                });
            }
        }

        public void setIcon(Skin value) {
            if (!visibleByDefault) {
                return;
            }
            icon = value;
            send(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null));
            Mundo.sync(1, () -> {
                for (Player player : oldTablist.players) {
                    Optional<Personal> personalOptional = personalTabs.get(player);
                    if (personalOptional == null) {
                        send(showPacket(), player);
                    } else {
                        personalOptional.ifPresent(tab -> {
                            send(tab.showPacket(), player);
                            removeIfApplicable(tab);
                        });
                    }
                }
            });
        }

        public void setScore(Integer value) {
            if (!visibleByDefault) {
                return;
            }
            super.setScore(value);
            for (Optional<Personal> personalOptional : personalTabs.values()) {
                personalOptional.ifPresent(tab -> {
                    tab.score = null;
                    removeIfApplicable(tab);
                });
            }
        }
    }

    public static class Personal extends Tab {
        public final Player player;
        public final Personalizable parent;

        public Personal(Personalizable parent, Player player) {
            super(parent.oldTablist, parent.name, parent.uuid, null, null, null, null);
            this.parent = parent;
            this.player = player;
        }

        @Override
        public void send(PacketContainer packet) {
            send(packet, player);
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
            if (value == null && displayName == null) {
                return;
            }
            displayName = value;
            send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, value == null ? parent.displayName : value, null, null));
            parent.removeIfApplicable(this);
        }

        @Override
        public void setLatency(Byte value) {
            if (value == null && latency == null) {
                return;
            }
            latency = value;
            send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, value == null ? parent.latency : value, null));
            parent.removeIfApplicable(this);
        }

        @Override
        public void setIcon(Skin value) {
            if (value == null && icon == null) {
                return;
            }
            icon = value;
            send(hidePacket());
            Mundo.sync(1, () -> send(showPacket()));
            parent.removeIfApplicable(this);
        }

        @Override
        public void setScore(Integer value) {
            if (value == null && score == null) {
                return;
            }
            score = value;
            if (oldTablist.areScoresEnabled()) {
                send(updateScorePacket(value == null ? parent.score : value));
            }
            parent.removeIfApplicable(this);
        }
    }
}
