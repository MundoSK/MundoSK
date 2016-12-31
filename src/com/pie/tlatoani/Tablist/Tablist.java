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
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Skin.SkinManager;
import com.pie.tlatoani.Tablist.Array.ArrayTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static HashMap<Player, Tablist> tablistMap = new HashMap<>();
    private static ArrayList<UUID> playersRespawning = new ArrayList<>();


    static {

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.PLAYER_INFO) {
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
                            HashMap<Player, String> tablistNames = tablist.tablistNames;
                            Mundo.debug(Tablist.class, "tablistNames = " + tablistNames);
                            String tablistName = tablist.tablistNames.get(player);
                            Mundo.debug(Tablist.class, "tablistName = " + tablistName);
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
                if (player != null && event.getPlayer() != null && getTablistForPlayer(event.getPlayer()).isPlayerHidden(player) && !event.isCancelled()) {
                    Mundo.debug(Tablist.class, "Player is hidden, event.getplayer = " + event.getPlayer().getName() + ", player = " + player.getName());
                    PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(colorStringToJson(player.getPlayerListName())));
                    PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packet.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
                    packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(event.getPlayer(), packet);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    removePacket.getPlayerInfoDataLists().writeSafely(0, Collections.singletonList(playerInfoData));
                    removePacket.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    Mundo.sync(new PacketSender(removePacket, event.getPlayer()));
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.RESPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                if (player != null && getTablistForPlayer(event.getPlayer()).isPlayerHidden(player) && !playersRespawning.contains(player.getUniqueId()) && !event.isCancelled()) {
                    Mundo.debug(Tablist.class, "Player is hidden");
                    playersRespawning.add(player.getUniqueId());
                    PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(player), 5, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromJson(colorStringToJson(player.getPlayerListName())));
                    PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                    packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    PacketContainer removePacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    removePacket.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
                    removePacket.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                    Mundo.syncDelay(2, new Runnable() {
                        @Override
                        public void run() {
                            playersRespawning.remove(player.getUniqueId());
                            try {
                                ProtocolLibrary.getProtocolManager().sendServerPacket(player, removePacket);
                            } catch (InvocationTargetException e) {
                                Mundo.reportException(Tablist.class, e);
                            }
                        }
                    });
                }
            }
        });
    }

    public static class PacketSender implements Runnable {
        private PacketContainer packet;
        private Player[] players;

        public PacketSender(PacketContainer packet, Player... players) {
            this.packet = packet;
            this.players = players;
        }

        @Override
        public void run() {
            for (Player player : players) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void onJoin(Player player) {
        if (!tablistMap.containsKey(player)) {
            setTablistForPlayer(Arrays.asList(player), new Tablist());

        }
        Mundo.syncDelay(1, new Runnable() {
            @Override
            public void run() {
                Set<Tablist> tablistSet = new HashSet<Tablist>(tablistMap.values());
                for (Tablist tablist : tablistSet) {
                    if (tablist.areAllPlayersHidden()) {
                        tablist.hidePlayers(Arrays.asList(player));
                    }
                }
            }
        });
    }

    public static void onQuit(Player player) {
        getTablistForPlayer(player).removePlayers(Arrays.asList(player));
        Set<Tablist> tablistSet = new HashSet<Tablist>(tablistMap.values());
        for (Tablist tablist : tablistSet) {
            tablist.hiddenPlayers.remove(player);
        }
    }

    public static Tablist getTablistForPlayer(Player player) {
        return tablistMap.get(player);
    }

    public static void setTablistForPlayer(Collection<Player> players, Tablist newTablist) {
        for (Player player : players) {
            Tablist oldTablist = getTablistForPlayer(player);
            if (oldTablist != null) oldTablist.removePlayers(Arrays.asList(player));
            tablistMap.put(player, newTablist);
        }
        newTablist.addPlayers(players);
    }

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
    }

    private void removePlayers(Collection<Player> players) {
        this.players.removeAll(players);
        simpleTablist.removePlayers(players);
        arrayTablist.removePlayers(players);
        showInTablist(hiddenPlayers, players);
        if (scoresEnabled) disableTablistObjective(players);
    }

    public void hidePlayers(Collection<Player> playersToHide) {
        Mundo.debug(this, "Hiding all players");
        hideInTablist(playersToHide, players);
        hiddenPlayers.addAll(playersToHide);
    }

    public void showPlayers(Collection<Player> playersToShow) {
        if (allPlayersHidden) {
            arrayTablist.setColumns(0);
        }
        allPlayersHidden = false;
        showInTablist(playersToShow, players);
        hiddenPlayers.removeAll(playersToShow);
    }

    public void hideAllPlayers() {
        if (!allPlayersHidden) {
            allPlayersHidden = true;
            Mundo.debug(this, "Hiding all players");
            hidePlayers(new ArrayList<>(Bukkit.getOnlinePlayers()));
        }
    }

    public void showAllPlayers() {
        arrayTablist.setColumns(0);
        allPlayersHidden = false;
        showInTablist(Bukkit.getOnlinePlayers(), players);
        hiddenPlayers.clear();
    }

    public boolean isPlayerHidden(Player player) {
        return allPlayersHidden || hiddenPlayers.contains(player);
    }

    public boolean areAllPlayersHidden() {
        return allPlayersHidden;
    }

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
                scores.clear();
            }
        }
    }

    public String getTablistName(Player player) {
        return tablistNames.getOrDefault(player, SkinManager.getTablistName(player));
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
    }

    //Hide/Unhide Static Methods

    public static void hideInTablist(Collection<? extends Player> objects, Collection<Player> subjects) {
        for (Player object : objects) {
            PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(object), 5, EnumWrappers.NativeGameMode.fromBukkit(object.getGameMode()), WrappedChatComponent.fromJson(Tablist.colorStringToJson(object.getPlayerListName())));
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
            packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
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
            PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(object), 5, EnumWrappers.NativeGameMode.fromBukkit(object.getGameMode()), WrappedChatComponent.fromJson(Tablist.colorStringToJson(object.getPlayerListName())));
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
            packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            try {
                for (Player subject : subjects) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(subject, packet);
                }
            } catch (InvocationTargetException e) {
                Mundo.reportException(Tablist.class, e);
            }
        }
    }

    public static void updateTablistName(Collection<? extends Player> objects, Collection<Player> subjects) {
        for (Player object : objects) {
            PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(object), 5, EnumWrappers.NativeGameMode.fromBukkit(object.getGameMode()), WrappedChatComponent.fromJson(Tablist.colorStringToJson(object.getPlayerListName())));
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoDataLists().writeSafely(0, Arrays.asList(playerInfoData));
            packet.getPlayerInfoAction().writeSafely(0, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
            try {
                for (Player subject : subjects) {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(subject, packet);
                }
            } catch (InvocationTargetException e) {
                Mundo.reportException(Tablist.class, e);
            }
        }
    }

    public static void updateScore(Player object, Collection<Player> subjects, Integer score) {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().writeSafely(0, SkinManager.getNameTag(object));
        packet.getStrings().writeSafely(1, Tablist.OBJECTIVE_NAME);
        packet.getIntegers().writeSafely(0, score);
        packet.getScoreboardActions().writeSafely(0, EnumWrappers.ScoreboardAction.CHANGE);
        try {
            for (Player subject : subjects) {
                ProtocolLibrary.getProtocolManager().sendServerPacket(subject, packet);
            }
        } catch (InvocationTargetException e) {
            Mundo.reportException(Tablist.class, e);
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
