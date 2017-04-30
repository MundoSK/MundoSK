package com.pie.tlatoani.Tablist.Tab;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 4/9/17.
 */
public interface Tab {

    Tablist getTablist();

    void addPlayer(Player player);

    void removePlayer(Player player);

    PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon);

    default PacketContainer showPacket() {
        return playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER, getDisplayName(), getLatency(), getIcon());
    }

    default PacketContainer hidePacket() {
        return playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null);
    }

    PacketContainer updateScorePacket(Integer score);

    void send(PacketContainer packet);

    void send(PacketContainer packet, Player to);

    String getDisplayName();

    Byte getLatency();

    Skin getIcon();

    Integer getScore();

    void setDisplayName(String value);

    void setLatency(Byte value);

    void setIcon(Skin value);

    void setScore(Integer value);

    class OldPersonalizable implements Tab {
        protected HashMap<Player, Optional<Personal>> personalTabs = new HashMap<>();
        protected Base baseTab;
        protected boolean visibleByDefault;

        public OldPersonalizable(Tablist.Storage storage, String name, UUID uuid) {
            baseTab = new Base(storage, name, uuid, null, null, null, null);
            visibleByDefault = false;
        }

        public OldPersonalizable(Base base) {
            baseTab = base;
            visibleByDefault = true;
        }

        public void showForAll() {
            if (visibleByDefault) {
                PacketContainer hidePacket = hidePacket();
                personalTabs.entrySet().removeIf(entry -> {
                    if (!entry.getValue().isPresent()) {
                        send(hidePacket, entry.getKey());
                        return true;
                    }
                    return false;
                });
            } else {
                visibleByDefault = true;
                send(showPacket());
            }
        }

        public void hideForAll() {
            if (visibleByDefault) {
                visibleByDefault = false;
                send(hidePacket());
            } else {
                UtilPacketEvent.sendPacket(hidePacket(), this, personalTabs.keySet());
            }
            personalTabs.clear();
        }

        protected Optional<Personal> forPlayer(Player player) {
            return personalTabs.get(player);
        }

        public Optional<? extends Tab> viewForPlayer(Player player) {
            Optional<Personal> personalOptional = forPlayer(player);
            if (personalOptional == null) {
                return Optional.of(this);
            }
            return personalOptional;
        }

        public Personal forceForPlayer(Player player) {
            Optional<Personal> personalOptional = forPlayer(player);
            if (personalOptional == null) {
                Personal personal = new Personal(this, player);
                personalTabs.put(player, Optional.of(personal));
                if (!visibleByDefault) {
                    personal.send(personal.showPacket());
                }
                return personal;
            } else {
                return personalOptional.orElse(null);
            }
        }

        public Personal forceForPlayer(Player player, String displayName, Byte latency, Skin icon) {
            Optional<Personal> personalOptional = forPlayer(player);
            if (personalOptional != null && personalOptional.isPresent()) {
                Personal personal = personalOptional.get();
                personal.setIcon(icon);
                return personal;
            } else {
                Personal personal = new Personal(this, player);
                if (personalOptional == null && visibleByDefault) {
                    personal.send(personal.hidePacket());
                }
                personal.displayName = Optional.ofNullable(displayName);
                personal.latency = Optional.ofNullable(latency);
                personal.icon = Optional.ofNullable(icon);
                personal.send(personal.showPacket());
                personalTabs.put(player, Optional.of(personal));
                return personal;
            }
        }

        public boolean visibleFor(Player player) {
            Optional<Personal> personalOptional = forPlayer(player);
            if (personalOptional == null) {
                return visibleByDefault;
            }
            return personalOptional.isPresent();
        }

        public void showFor(Player player) {
            Optional<Personal> personalOptional = forPlayer(player);
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

        public void hideFor(Player player) {
            send(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null), player);
            if (visibleByDefault) {
                personalTabs.put(player, Optional.empty());
            } else {
                personalTabs.remove(player);
            }
        }

        public void removePlayer(Player player) {
            Optional<Personal> personalOptional = forPlayer(player);
            if (personalOptional == null) {
                if (visibleByDefault) {
                    send(hidePacket(), player);
                }
            } else {
                personalOptional.ifPresent(__ -> send(hidePacket(), player));
                personalTabs.remove(player);
            }
        }

        @Override
        public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
            return baseTab.playerInfoPacket(action, displayName, latency, icon);
        }

        @Override
        public PacketContainer updateScorePacket(Integer score) {
            return baseTab.updateScorePacket(score);
        }

        @Override
        public void send(PacketContainer packet) {
            baseTab.send(packet);
        }

        @Override
        public void send(PacketContainer packet, Player to) {
            baseTab.send(packet, to);
        }

        @Override
        public String getDisplayName() {
            return baseTab.getDisplayName();
        }

        @Override
        public Byte getLatency() {
            return baseTab.getLatency();
        }

        @Override
        public Skin getIcon() {
            return baseTab.getIcon();
        }

        @Override
        public Integer getScore() {
            return baseTab.getScore();
        }

        public void setDisplayName(String value) {
            if (!visibleByDefault) {
                return;
            }
            baseTab.setDisplayName(value);
            personalTabs.values().removeIf(personalOptional ->
                Mundo.optionalCase(personalOptional, personal -> {
                    personal.displayName = null;
                    return personal.containsValue();
                }, () -> false)
            );
        }

        public void setLatency(Byte value) {
            if (!visibleByDefault) {
                return;
            }
            baseTab.setLatency(value);
            personalTabs.values().removeIf(personalOptional ->
                    Mundo.optionalCase(personalOptional, personal -> {
                        personal.latency = null;
                        return personal.containsValue();
                    }, () -> false)
            );
        }

        public void setIcon(Skin value) {
            if (!visibleByDefault) {
                return;
            }
            baseTab.icon = value;
            send(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null));
            Mundo.sync(1, () -> {
                for (Player player : baseTab.storage.players) {
                    Optional<Personal> personalOptional = forPlayer(player);
                    if (personalOptional == null) {
                        send(showPacket(), player);
                    } else {
                        personalOptional.ifPresent(tab -> {
                            send(tab.showPacket(), player);
                            if (!tab.containsValue()) {
                                personalTabs.remove(player);
                            }
                        });
                    }
                }
            });
        }

        public void setScore(Integer value) {
            if (!visibleByDefault) {
                return;
            }
            baseTab.setScore(value);
            personalTabs.values().removeIf(personalOptional ->
                    Mundo.optionalCase(personalOptional, personal -> {
                        personal.score = null;
                        return personal.containsValue();
                    }, () -> false)
            );
        }
    }

}
