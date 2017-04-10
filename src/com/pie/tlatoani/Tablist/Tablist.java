package com.pie.tlatoani.Tablist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Skin.SkinManager;
import com.pie.tlatoani.Tablist.Array.ArrayTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO;

/**
 * Created by Tlatoani on 11/24/16.
 */
public class Tablist {
    public final Set<Player> players = new HashSet<>();
    public final SimpleTablist simpleTablist = new SimpleTablist(this);
    public final ArrayTablist arrayTablist = new ArrayTablist(this);
    private Set<Player> hiddenPlayers = new HashSet<>();
    private HashMap<Player, String> tablistNames = new HashMap<>();
    private HashMap<Player, Integer> scores = new HashMap<>();
    private boolean allPlayersHidden = false;
    private boolean scoresEnabled = false;

    public static final String OBJECTIVE_NAME = "MundoSK_Tablist";
    public static final Skin DEFAULT_SKIN_TEXTURE = new Skin.Simple(
            "eyJ0aW1lc3RhbXAiOjE0NzAwMjgwNDU3MzUsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNTg3OTM1YzdmYmVjYzJmYWMxMDY0OWZjZGZiODM1YjQ2NTA3MzZiOWJmMWQ0NGVhZjc2ZDNiOWVmN2UwIn19fQ==",
            "eTy8+/waBl22GpAyTHx+QY40J3DY57F2FSkVupjJxAuuUfstvX/DxmJANKtIcYCYP9LUHh9DkP1T2bXUobHcx8GAICi8S/uEWXx96PHHjSr7wQ9uBC4NMCkV7dHHMKdVqEJ9jDpMvSax9vs1tOc2NWaeMbzc/345K95JaYVD+AV4W1+IuppXlMgDmCatUCgGDbzTuQKO8An9zFPciCRq1VSGaOPCj4PoIDQyMhSPqb1cPML/wH26Wtl4DEjnyVIyemk7oDBK29DXxtBLmzX6Ni1C8VM3UmG2StDC7dSwxJNLBHQ/aqXwupK4j0bZghiRbiaq4kAlPcpMeL+TTHac7oYFGihj/s/OVWaL0Fo2KgFZgKuZ26kDepCLEEOOoj2Zq8ohtxufPdTDqw032AyA/HbldnBIsCnQCDiq3XXdZHz0R+pvuf73BSHc7CiG2pwjSdSQ8XetlP70A9SddJu+iFuKGwzh/cvQ2H+sqoUYmIYIXcl2xJTy+Y/shxJDZZVxGCSHmj+4SYzJCg+nsNlEJ9HBG//LfeY+WhacbC9pPPy8wKnDqvIx0QX2YakyBFy659DEBEhSSNRQjOm78Zd9K7pP1QOrS2RDwsDSIXaR0gxT69Bv+Z/r+w8GJY6tHvT8aqTNQHpmv+kwMVdGOWMj3wMErW2aqjH9ffc1nuWht/E="
    );
    public static final Tablist GLOBAL = new Tablist();
    private static final HashMap<Player, Tablist> tablistMap = new HashMap<>();
    private static final ArrayList<Player> playersRespawning = new ArrayList<>();

    private WeakHashMap<Player, PlayerOldTab> tabs;

    public static class PlayerOldTab extends OldTab.VariablyVisible {
        private final Player player;

        public PlayerOldTab(Tablist tablist, Player player, boolean initialVisibility) {
            super(tablist, player.getName(), player.getUniqueId(), null, null, null, null, initialVisibility);
            this.player = player;
            this.icons = null;
        }

        //Because the changes will be made by intercepting the packet anyways, there is no need for the initial packet to contain new info
        @Override
        protected void sendPacket(Player target, EnumWrappers.PlayerInfoAction action, String displayName, Byte latency, Skin icon) {
            PacketContainer packet = playerInfoPacket(player, action);
            if (target != null) {
                UtilPacketEvent.sendPacket(packet, this, target);
            } else {
                UtilPacketEvent.sendPacket(packet, this, tablist.players.toArray(new Player[0]));
            }
        }

        //Not sure exactly why I'm putting the safety code here instead of Tablist#showTab
        @Override
        public void showFor(Player target, String displayName, Byte latency, Skin icon, Integer score) {
            tablist.allPlayersHidden = false;
            if (tablist.arrayTablist.getColumns() != 4 || tablist.arrayTablist.getColumns() != 20) {
                tablist.arrayTablist.setColumns(0);
            }
            super.showFor(player, displayName, latency, icon, score);
        }

        //Done so that there doesn't need to be a separate method when you want to hide a player for the whole tablist
        @Override
        public void hideFor(Player target) {
            super.hideFor(target);
            if (!visibleForAnyone()) {
                tablist.hiddenPlayers.add(player);
                tablist.tabs.remove(player);
            }
        }

        //Used when adding a player to the tablist
        public void defaultFor(Player target) {
            if (visibleFor(null)) {
                String displayName = displayNames.getDefaultValue();
                if (displayNames.getDefaultValue() != null) {
                    sendPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME, displayName, null, null);
                }
                Byte latency = latencies.getDefaultValue();
                if (latency != null) {
                    sendPacket(player, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY, null, latency, null);
                }
                if (tablist.scoresEnabled) {
                    Integer score = scores.getDefaultValue();
                    if (score != null) {
                        updateScore(player, score);
                    }
                }
            }
        }

        //The following is done because modifying the icon will change the player's own skin, so it should not be handled by Tablist

        @Override
        public Skin getIcon(Player target) {
            throw new UnsupportedOperationException("PlayerTabs do not support skin modification!");
        }

        @Override
        public void setIcon(Player target, Skin value) {
            throw new UnsupportedOperationException("PlayerTabs do not support skin modification!");
        }

    }

    static {

        if (Mundo.implementPacketStuff) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PLAYER_INFO) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (!event.isCancelled()) {
                        List<PlayerInfoData> playerInfoDatas = event.getPacket().getPlayerInfoDataLists().readSafely(0);
                        List<PlayerInfoData> newPlayerInfoDatas = new ArrayList<PlayerInfoData>();
                        for (PlayerInfoData playerInfoData : playerInfoDatas) {
                            Player player = Bukkit.getPlayer(playerInfoData.getProfile().getUUID());
                            PlayerInfoData newPlayerInfoData = playerInfoData;
                            if (player != null && event.getPlayer() != null) {
                                Tablist tablist = getTablistForPlayer(event.getPlayer());
                                Mundo.debug(Tablist.class, "getTablistForPlayer = " + tablist);
                                OldTab oldTab = tablist.getTab(player);
                                Mundo.debug(Tablist.class, "getTab = " + oldTab);
                                if (oldTab == null) {
                                    continue;
                                }
                                //HashMap<Player, String> tablistNames = tablist.tablistNames;
                                //Mundo.debug(Tablist.class, "tablistNames = " + tablistNames);
                                //String tablistName = tablist.tablistNames.get(player);
                                //Mundo.debug(Tablist.class, "tablistName = " + tablistName);
                                String tablistName = Mundo.firstNotNull(oldTab.getDisplayName(event.getPlayer()), oldTab.getDisplayName(null));
                                if (tablistName != null) {
                                    newPlayerInfoData = new PlayerInfoData(playerInfoData.getProfile(), playerInfoData.getLatency(), playerInfoData.getGameMode(), WrappedChatComponent.fromJson(colorStringToJson(tablistName)));
                                }
                            }
                            newPlayerInfoDatas.add(newPlayerInfoData);
                        }
                        event.getPacket().getPlayerInfoDataLists().writeSafely(0, newPlayerInfoDatas);
                    }
                }
            });

            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    Player player = Bukkit.getPlayer(event.getPacket().getUUIDs().read(0));
                    //if (player != null && event.getPlayer() != null && getTablistForPlayer(event.getPlayer()).isPlayerHidden(player) && !event.isCancelled()) {
                    if (event.isCancelled() || player == null || event.getPlayer() == null) {
                        return;
                    }
                    PlayerOldTab tab = getTablistForPlayer(event.getPlayer()).getTab(player);
                    if (tab == null || !tab.visibleFor(event.getPlayer())) {
                        Mundo.debug(Tablist.class, "Player is hidden, event.getplayer = " + event.getPlayer().getName() + ", player = " + player.getName());
                        /*PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(colorStringToJson(player.getPlayerListName())));
                        PacketContainer addPacket = new PacketContainer(PLAYER_INFO);
                        addPacket.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
                        addPacket.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), addPacket);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        PacketContainer removePacket = new PacketContainer(PLAYER_INFO);
                        removePacket.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
                        removePacket.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                        Mundo.sync(Mundo.tablistRemoveTabDelaySpawn, new PacketSender(removePacket, event.getPlayer()));*/
                        UtilPacketEvent.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), Tablist.class, event.getPlayer());
                        Mundo.sync(Mundo.tablistRemoveTabDelaySpawn, () -> UtilPacketEvent.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), Tablist.class, event.getPlayer()));
                    }
                }
            });

            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.RESPAWN) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    Player player = event.getPlayer();
                    //if (player != null && getTablistForPlayer(event.getPlayer()).isPlayerHidden(player) && !playersRespawning.contains(player.getUniqueId()) && !event.isCancelled()) {
                    if (event.isCancelled() || player == null || playersRespawning.contains(player)) {
                        return;
                    }
                    PlayerOldTab tab = getTablistForPlayer(player).getTab(player);
                    if (tab == null || !tab.visibleFor(player)) {
                        Mundo.debug(Tablist.class, "Player is hidden = " + player.getName());
                        playersRespawning.add(player);
                        /*PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(colorStringToJson(player.getPlayerListName())));
                        PacketContainer packet = new PacketContainer(PLAYER_INFO);
                        packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                        packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                        try {
                            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        PacketContainer removePacket = new PacketContainer(PLAYER_INFO);
                        removePacket.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                        removePacket.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                        Mundo.sync(Mundo.tablistRemoveTabDelayRespawn, new Runnable() {
                            @Override
                            public void run() {
                                playersRespawning.remove(player.getUniqueId());
                                try {
                                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, removePacket);
                                } catch (InvocationTargetException e) {
                                    Mundo.reportException(Tablist.class, e);
                                }
                            }
                        });*/
                        UtilPacketEvent.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), Tablist.class, player);
                        Mundo.sync(Mundo.tablistRemoveTabDelaySpawn, () -> {
                            playersRespawning.remove(player);
                            UtilPacketEvent.sendPacket(playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), Tablist.class, player);
                        });
                    }
                }
            });
        }


    }

    public static void onJoin(Player player) {
        if (!tablistMap.containsKey(player)) {
            //setTablistForPlayer(Arrays.asList(player), new Tablist());
            setTablistOfPlayer(player, GLOBAL);
        }
        Mundo.sync(1, () -> {
            Set<Tablist> tablistSet = new HashSet<Tablist>(tablistMap.values());
            for (Tablist tablist : tablistSet) {
                if (tablist.areAllPlayersHidden()) {
                    //tablist.hidePlayers(Arrays.asList(player));
                    PlayerOldTab tab = tablist.getTab(player);
                    if (tab != null) {
                        tab.hideFor(null);
                    }
                }
            }
        });
    }

    public static void onQuit(Player player) {
        //getTablistForPlayer(player).removePlayer(player);
        getTablistForPlayer(player).players.remove(player);
        Set<Tablist> tablistSet = new HashSet<Tablist>(tablistMap.values());
        for (Tablist tablist : tablistSet) {
            tablist.hiddenPlayers.remove(player);
            tablist.tabs.remove(player);
        }
    }

    public static Tablist getTablistForPlayer(Player player) {
        return tablistMap.get(player);
    }

    /*
    public static void setTablistForPlayer(Collection<Player> players, Tablist newTablist) {
        for (Player player : players) {
            Tablist oldTablist = getTablistForPlayer(player);
            if (oldTablist != null) oldTablist.removePlayer(player);
            tablistMap.put(player, newTablist);
        }
        newTablist.addPlayers(players);
    }*/

    public static void setTablistOfPlayer(Player player, Tablist newTablist) {
        getTablistForPlayer(player).removePlayer(player);
        newTablist.addPlayer(player);
        tablistMap.put(player, newTablist);
    }

    public static boolean isPlayerHiddenFor(Player player, Player target) {
        PlayerOldTab tab = getTablistForPlayer(target).getTab(player);
        return tab == null || !tab.visibleFor(target);
    }

    //New tablist method
    public PlayerOldTab getTab(Player player) {
        if (hiddenPlayers.contains(player)) {
            return null;
        }
        PlayerOldTab playerTab = tabs.get(player);
        if (playerTab == null) {
            playerTab = new PlayerOldTab(this, player, true);
            tabs.put(player, playerTab);
        }
        return playerTab;
    }

    //New tablist method
    public void showTab(Player tabOf, Player target) {
        PlayerOldTab tab = getTab(tabOf);
        if (tab == null) {
            tab = new PlayerOldTab(this, tabOf, false);
            tab.showFor(target, null, null, null, null);
            tabs.put(tabOf, tab);
        }
    }

    /*
    private void addPlayers(Collection<Player> players) {
        this.players.addAll(players);
        simpleTablist.addPlayers(players);
        arrayTablist.addPlayers(players);
        hideInTablist(hiddenPlayers, players);
        ArrayList<Player> visiblePlayers = new ArrayList<>();
        visiblePlayers.addAll(Bukkit.getOnlinePlayers());
        visiblePlayers.removeAll(hiddenPlayers);
        updateTablistName(visiblePlayers, players);
        if (scoresEnabled) {
            enableTablistObjective(players);
            for (Player visiblePlayer : visiblePlayers) {
                updateScore(visiblePlayer, players, getScore(visiblePlayer));
            }
        }
    }*/


    private void addPlayer(Player player) {
        this.players.add(player);
        if (scoresEnabled) {
            enableTablistObjective(Collections.singleton(player));
        }
        tabs.values().forEach(tab -> tab.defaultFor(player));
        hideInTablist(hiddenPlayers, Collections.singleton(player));
        simpleTablist.addPlayer(player);
        arrayTablist.addPlayer(player);
    }

    //Should be good to go for new tablist
    private void removePlayer(Player player) {
        this.players.remove(player);
        simpleTablist.removePlayer(player);
        arrayTablist.removePlayer(player);
        showInTablist(hiddenPlayers, Collections.singleton(player));
        if (scoresEnabled) {
            disableTablistObjective(Collections.singleton(player));
        }
    }

    /*
    public void hidePlayers(Collection<Player> playersToHide) {
        Mundo.debug(this, "Hiding all players");
        hideInTablist(playersToHide, players);
        hiddenPlayers.addAll(playersToHide);
    }*/

    /*
    public void showPlayers(Collection<Player> playersToShow) {
        if (allPlayersHidden && (arrayTablist.getColumns() != 4 || arrayTablist.getRows() != 20)) {
            arrayTablist.setColumns(0);
        }
        allPlayersHidden = false;
        showInTablist(playersToShow, players);
        hiddenPlayers.removeAll(playersToShow);
    }*/

    //Should be good now
    public void hideAllPlayers() {
        /*if (!allPlayersHidden) {
            allPlayersHidden = true;
            Mundo.debug(this, "Hiding all players");
            hidePlayers(new ArrayList<>(Bukkit.getOnlinePlayers()));
        }*/
        if (!allPlayersHidden) {
            allPlayersHidden = true;
            Mundo.debug(this, "Hiding all players");
            List<PlayerOldTab> tempTabs = new ArrayList<>(tabs.values());
            tempTabs.forEach(tab -> tab.hideFor(null));
        }
    }

    //Should be good now
    public void showAllPlayers() {
        /*if (arrayTablist.getColumns() != 4 || arrayTablist.getRows() != 20) {
            arrayTablist.setColumns(0);
        }
        allPlayersHidden = false;
        showInTablist(Bukkit.getOnlinePlayers(), players);
        hiddenPlayers.clear();*/
        Bukkit.getOnlinePlayers().forEach(player -> showTab(player, null));
    }

    /*
    public boolean isPlayerHidden(Player player) {
        return allPlayersHidden || hiddenPlayers.contains(player);
    }*/

    public boolean areAllPlayersHidden() {
        return allPlayersHidden;
    }

    //The following two methods are good to go

    public boolean areScoresEnabled() {
        return scoresEnabled;
    }

    public void setScoresEnabled(boolean value) {
        if (scoresEnabled != value) {
            scoresEnabled = value;
            if (scoresEnabled) {
                enableTablistObjective(players);
            } else {
                disableTablistObjective(players);
                //scores.clear();
            }
        }
    }

    /*
    public String getTablistName(Player player) {
        return tablistNames.get(player);
    }

    public void setTablistName(Player player, String value) {
        tablistNames.put(player, value);
        if (!isPlayerHidden(player)) {
            updateTablistName(Collections.singleton(player), players);
        }
    }

    public Integer getScore(Player player) {
        return scores.getOrDefault(player, 0);
    }

    public void setScore(Player player, int score) {
        if (!scoresEnabled) return;
        scores.put(player, score);
        updateScore(player, players, score);
    }*/

    //Static Utility Methods

    public static PacketContainer playerInfoPacket(
            String displayName,
            Integer latency,
            GameMode gameMode,
            String name,
            UUID uuid,
            Skin skin,
            EnumWrappers.PlayerInfoAction action
    ) {
        PacketContainer result = new PacketContainer(PLAYER_INFO);
        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (skin == null) {
                skin = DEFAULT_SKIN_TEXTURE;
            }
            skin.retrieveSkinTextures(profile.getProperties());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(
                profile,
                Mundo.firstNotNull(latency, 5),
                Mundo.firstNotNull(EnumWrappers.NativeGameMode.fromBukkit(gameMode), EnumWrappers.NativeGameMode.NOT_SET),
                WrappedChatComponent.fromJson(colorStringToJson(Mundo.firstNotNull(displayName, "")))
        );
        result.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        result.getPlayerInfoAction().writeSafely(0, action);
        return result;
    }

    public static PacketContainer playerInfoPacket(Player player, EnumWrappers.PlayerInfoAction action) {
        PacketContainer result = new PacketContainer(PLAYER_INFO);
        PlayerInfoData playerInfoData = new PlayerInfoData(
                WrappedGameProfile.fromPlayer(player),
                5,
                EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()),
                WrappedChatComponent.fromText(player.getPlayerListName())
        );
        result.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
        result.getPlayerInfoAction().writeSafely(0, action);
        return result;
    }

    public static PacketContainer scorePacket(String scoreName, String objectiveName, Integer score, EnumWrappers.ScoreboardAction action) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().writeSafely(0, scoreName);
        packet.getStrings().writeSafely(1, objectiveName);
        packet.getIntegers().writeSafely(0, Mundo.firstNotNull(score, 0));
        packet.getScoreboardActions().writeSafely(0, action);
        return packet;
    }

    public static void hideInTablist(Collection<? extends Player> objects, Collection<Player> subjects) {
        for (Player object : objects) {
            //PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(object), 5, EnumWrappers.NativeGameMode.fromBukkit(object.getGameMode()), WrappedChatComponent.fromJson(Tablist.colorStringToJson(object.getPlayerListName())));
            //PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            //packet.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
            //packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            PacketContainer packet = playerInfoPacket(object.getPlayerListName(), 5, object.getGameMode(), object.getName(), object.getUniqueId(), null, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            try {
                for (Player subject : subjects) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(subject, packet);
                }
            } catch (InvocationTargetException e) {
                Mundo.reportException(Tablist.class, e);
            }
        }
    }

    public static void showInTablist(Collection<? extends Player> objects, Collection<Player> subjects) {
        for (Player object : objects) {
            /*PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(object), 5, EnumWrappers.NativeGameMode.fromBukkit(object.getGameMode()), WrappedChatComponent.fromJson(Tablist.colorStringToJson(object.getPlayerListName())));
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
            packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);*/
            PacketContainer packet = playerInfoPacket(object.getPlayerListName(), 5, object.getGameMode(), object.getName(), object.getUniqueId(), SkinManager.getActualSkin(object), EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            try {
                for (Player subject : subjects) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(subject, packet);
                }
            } catch (InvocationTargetException e) {
                Mundo.reportException(Tablist.class, e);
            }
        }
    }

    public static void enableTablistObjective(Collection<? extends Player> players) {
        PacketContainer createPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_OBJECTIVE); //Used to get some defaults
        createPacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
        createPacket.getStrings().writeSafely(1, OBJECTIVE_NAME);
        createPacket.getIntegers().writeSafely(0, 0);
        PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        displayPacket.getIntegers().writeSafely(0, 0);
        displayPacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, createPacket);
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, displayPacket);
            }
        } catch (InvocationTargetException e) {
            Mundo.reportException(Tablist.class, e);
        }

    }

    public static void disableTablistObjective(Collection<? extends Player> players) {
        PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        removePacket.getStrings().writeSafely(0, OBJECTIVE_NAME);
        removePacket.getStrings().writeSafely(1, OBJECTIVE_NAME);
        //Something goes here
        removePacket.getIntegers().writeSafely(0, 1);
        try {
            for (Player player : players) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, removePacket);
            }
        } catch (InvocationTargetException e) {
            Mundo.reportException(Tablist.class, e);
        }
    }

    /*
    This method was coded by werter318 on Bukkit.org
     */
    public static String colorStringToJson(String original) {
        if (original == null) throw new NullPointerException("original String cannot be null!");
        char colorChar = ChatColor.COLOR_CHAR;

        String template = "{text:\"TEXT\",color:COLOR,bold:BOLD,underlined:UNDERLINED,italic:ITALIC,strikethrough:STRIKETHROUGH,obfuscated:OBFUSCATED,extra:[EXTRA]}";
        String json = "";

        List<String> parts = new ArrayList<>();

        int first = 0;
        int last = 0;

        while ((first = original.indexOf(colorChar, last)) != -1) {
            int offset = 2;
            while ((last = original.indexOf(colorChar, first + offset)) - 2 == first) {
                offset += 2;
            }

            if (last == -1) {
                parts.add(original.substring(first));
                break;
            } else {
                parts.add(original.substring(first, last));
            }
        }

        if (parts.isEmpty()) {
            parts.add(original);
        }

        Pattern colorFinder = Pattern.compile("(" + colorChar + "([a-f0-9]))");
        for (String part : parts) {
            json = (json.isEmpty() ? template : json.replace("EXTRA", template));

            Matcher matcher = colorFinder.matcher(part);
            ChatColor color = (matcher.find() ? ChatColor.getByChar(matcher.group().charAt(1)) : ChatColor.WHITE);

            json = json.replace("COLOR", color.name().toLowerCase());
            json = json.replace("BOLD", String.valueOf(part.contains(ChatColor.BOLD.toString())));
            json = json.replace("ITALIC", String.valueOf(part.contains(ChatColor.ITALIC.toString())));
            json = json.replace("UNDERLINED", String.valueOf(part.contains(ChatColor.UNDERLINE.toString())));
            json = json.replace("STRIKETHROUGH", String.valueOf(part.contains(ChatColor.STRIKETHROUGH.toString())));
            json = json.replace("OBFUSCATED", String.valueOf(part.contains(ChatColor.MAGIC.toString())));

            json = json.replace("TEXT", part.replaceAll("(" + colorChar + "([a-z0-9]))", ""));
        }

        json = json.replace(",extra:[EXTRA]", "");

        return json;
    }

}
