package com.pie.tlatoani.Tablist.Player;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab.Tab;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.Either;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/14/17.
 * Note: Currently, Personals given by PlayerPersonalizable allow you to change the icon
 * Please don't do it
 * I don't know who would do it anyways
 */
public class PlayerTablist {
    public final Tablist tablist;
    private final Tablist.Storage storage;
    private final HashMap<Player, Optional<Tab>> tabs = new HashMap<>();

    public PlayerTablist(Tablist.Storage storage) {
        this.tablist = storage.tablist;
        this.storage = storage;
    }

    public Tab getTab(Player player) {
        Optional<Tab> tabOptional = tabs.get(player);
        if (tabOptional == null) {
            return null;
        } else {
            return tabOptional.orElse(null);
        }
    }

    public Tab forceTab(Player player) {
        Optional<Tab> tabOptional = tabs.get(player);
        if (tabOptional == null) {
            Tab tab = new PlayerBase(storage, player);
            tabs.put(player, Optional.of(tab));
            return tab;
        } else {
            return tabOptional.orElse(null);
        }
    }

    public PlayerOldPersonalizable forcePersonalizable(Player player) {
        return tabs.get(player).map(tab -> {
            if (tab instanceof PlayerOldPersonalizable) {
                return (PlayerOldPersonalizable) tab;
            }
            PlayerOldPersonalizable personalizable1 = new PlayerOldPersonalizable(this, player, (PlayerBase) tab);
            tabs.put(player, Optional.of(personalizable1));
            return personalizable1;
        }).orElseGet(() -> {
            PlayerOldPersonalizable personalizable1 = new PlayerOldPersonalizable(this, player);
            tabs.put(player, Optional.of(personalizable1));
            return personalizable1;
        });
    }

    public boolean isPlayerVisible(Player player, Player target) {
        Optional<Tab> tabOptional = tabs.get(player);
        if (tabOptional == null) {
            return true;
        }
        return tabOptional.map(tab -> {
            if (tab instanceof PlayerOldPersonalizable) {
                return ((PlayerOldPersonalizable) tab).visibleFor(target);
            } else {
                return true;
            }
        }).orElse(false);
    }

    public void showAllPlayers() {
        storage.playerTablistOrVisibility = Either.right(true);
        tabs.forEach((player, tabOptional) -> {
            Mundo.optionalCase(tabOptional, tab -> {
                if (tab instanceof PlayerOldPersonalizable) {
                    ((PlayerOldPersonalizable) tab).showForAll();
                }
            }, () -> {
                UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), PlayerTablist.class, storage.players);
            });
        });
    }

    public void hideAllPlayers() {
        storage.playerTablistOrVisibility = Either.right(false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Optional<Tab> tabOptional = tabs.get(player);
            if (tabOptional == null) {
                UtilPacketEvent.sendPacket(TablistManager.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), PlayerTablist.class, storage.players);
            } else {
                tabOptional.ifPresent(tab -> {
                    if (tab instanceof PlayerOldPersonalizable) {
                        ((PlayerOldPersonalizable) tab).hideForAll();
                    } else {
                        tab.send(tab.hidePacket());
                    }
                });
            }
        }

    }

    public void addPlayer(Player player) {
        tabs.forEach(((objPlayer, tabOptional) -> {
            Mundo.optionalCase(tabOptional, tab -> {
                if (tab instanceof PlayerOldPersonalizable && !((PlayerOldPersonalizable) tab).isVisibleByDefault()) {
                    tab.send(tab.hidePacket(), player);
                } else {
                    if (tab.getDisplayName() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                    if (tab.getLatency() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, null, null), player);
                    }
                    if (tab.getScore() != null) {
                        tab.send(tab.updateScorePacket(tab.getScore()), player);
                    }
                }
            }, () -> {
                PacketContainer hidePacket = TablistManager.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                UtilPacketEvent.sendPacket(hidePacket, PlayerTablist.class, player);
            });
        }));
    }

    public void removePlayer(Player player) {
        tabs.forEach(((objPlayer, tabOptional) -> {
            Mundo.optionalCase(tabOptional, tab -> {
                if (tab instanceof PlayerOldPersonalizable) {
                    ((PlayerOldPersonalizable) tab).removePlayer(player);
                } else {
                    if (tab.getDisplayName() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                    if (tab.getLatency() != null) {
                        tab.send(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                }
            }, () -> {
                PacketContainer showPacket = TablistManager.playerInfoPacket(objPlayer, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                UtilPacketEvent.sendPacket(showPacket, PlayerTablist.class, player);
            });
        }));
    }

    private void removeTab(Player player) {
        tabs.remove(player);
        if (tabs.isEmpty()) {
            storage.playerTablistOrVisibility = Either.right(true);
        }
    }

    //Tab Class Modifications

    public static class PlayerBase extends Tab.Base {
        public final Player player;

        public PlayerBase(Tablist.Storage storage, Player player) {
            super(storage, player.getName(), player.getUniqueId(), null, null, null, null);
            this.player = player;
        }

        public PlayerBase(Player player, Base prev) {
            super(prev);
            this.player = player;
        }

        //The packet intercepter will make the required changes
        @Override
        public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
            return TablistManager.playerInfoPacket(player, action);
        }

        @Override
        public void setIcon(Skin value) {
            throw new UnsupportedOperationException("PlayerTablist does not allow you to change skin icons, as that would change the player's own skin");
        }
    }

    public static class PlayerOldPersonalizable extends Tab.OldPersonalizable {
        public final Player player;
        public final PlayerTablist playerTablist;

        public PlayerOldPersonalizable(PlayerTablist playerTablist, Player player) {
            super(playerTablist.storage, player.getName(), player.getUniqueId());
            this.player = player;
            this.playerTablist = playerTablist;
        }

        public PlayerOldPersonalizable(PlayerTablist playerTablist, Player player, Base base) {
            super(base);
            this.player = player;
            this.playerTablist = playerTablist;
        }

        //The packet intercepter will make the required changes
        @Override
        public PacketContainer playerInfoPacket(EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
            return TablistManager.playerInfoPacket(player, action);
        }

        @Override
        public void hideForAll() {
            super.hideForAll();
            playerTablist.tabs.put(player, Optional.empty());
        }

        @Override
        public void removePlayer(Player player) {
            Optional<Personal> personalOptional = forPlayer(player);
            if (personalOptional == null) {
                if (visibleByDefault) {
                    if (getDisplayName() != null) {
                        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null), player);
                    }
                    if (getLatency() != null) {
                        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, null, null), player);
                    }
                } else {
                    send(showPacket(), player);
                }
            } else {
                personalTabs.remove(player);
                Mundo.optionalCase(personalOptional, personal -> {
                    if (personal.getDisplayName() != null) {
                        personal.send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, null, null, null));
                    }
                    if (personal.getLatency() != null) {
                        personal.send(playerInfoPacket(EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, null, null));
                    }
                }, () -> send(showPacket(), player));
            }
        }

        @Override
        public void setIcon(Skin value) {
            throw new UnsupportedOperationException("PlayerTablist does not allow you to change skin icons, as that would change the player's own skin");
        }
    }
}
