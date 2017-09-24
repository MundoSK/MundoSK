package com.pie.tlatoani.Skin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.ProtocolLib.PacketManager;
import com.pie.tlatoani.ProtocolLib.PacketUtil;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Created by Tlatoani on 9/18/16.
 */
public class SkinManager {
    private static final HashMap<Player, Skin> actualSkins = new HashMap<>();
    private static final HashMap<Player, Skin> displayedSkins = new HashMap<>();
    private static final Table<Player, Player, Skin> personalDisplayedSkins = Tables.newCustomTable(new HashMap<>(), HashMap::new);
    private static final HashMap<Player, String> nameTags = new HashMap<>();
    private static final HashMap<Player, String> tabNames = new HashMap<>();

    private static final ArrayList<Player> spawnedPlayers = new ArrayList<>();

    private static Reflection.MethodInvoker CRAFT_PLAYER_GET_HANDLE = null;
    private static Reflection.MethodInvoker DEDICATED_PLAYER_LIST_MOVE_TO_WORLD = null;

    public static void loadReflectionStuff() {
        try {
            CRAFT_PLAYER_GET_HANDLE = Reflection.getTypedMethod(Reflection.getCraftBukkitClass("entity.CraftPlayer"), "getHandle", Reflection.getMinecraftClass("EntityPlayer"));
            DEDICATED_PLAYER_LIST_MOVE_TO_WORLD = Reflection.getMethod(Reflection.getMinecraftClass("DedicatedPlayerList"), "moveToWorld", Reflection.getMinecraftClass("EntityPlayer"), int.class, boolean.class, Location.class, boolean.class);
        } catch (Exception e) {
            Logging.reportException(SkinManager.class, e);
        }
    }

    public static void loadPacketEvents() {
        PacketManager.onPacketEvent(PacketType.Play.Server.PLAYER_INFO, event -> {
            if (event.getPacket().getPlayerInfoAction().read(0) == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                Logging.debug(SkinManager.class, "EVENT.GETPLAYER = " + event.getPlayer().getName());
                List<PlayerInfoData> playerInfoDatas = event.getPacket().getPlayerInfoDataLists().readSafely(0);
                List<PlayerInfoData> newPlayerInfoDatas = new ArrayList<PlayerInfoData>();
                for (PlayerInfoData playerInfoData : playerInfoDatas) {
                    Player player = Bukkit.getPlayer(playerInfoData.getProfile().getUUID());
                    PlayerInfoData newPlayerInfoData = playerInfoData;

                    if (player != null && !spawnedPlayers.contains(player)) {
                        Logging.debug(SkinManager.class, "NEW PLAYER !");
                        spawnedPlayers.add(player);
                        if (!actualSkins.containsKey(player)) {
                            if (!actualSkins.containsKey(player)) {
                                Skin skin = Skin.fromGameProfile(playerInfoData.getProfile());
                                Logging.debug(SkinManager.class, "ALTERNATIVE SKINTEXTURE FOUND IN PACKET = " + skin);
                                if (!skin.toString().equals("[]")) {

                                    actualSkins.put(player, skin);
                                    displayedSkins.put(player, skin);
                                }
                            }

                        }
                    }

                    if (player != null) {
                        Logging.debug(SkinManager.class, "Pre Namtatg: " + playerInfoData.getProfile().getName());
                        //Team team = player.getScoreboard().getEntryTeam(player.getName());
                        //String nameTag = team == null ? getNameTag(player) : team.getPrefix() + getNameTag(player) + team.getSuffix();
                        String nameTag = getNameTag(player);
                        String tabName = player.getPlayerListName();
                        newPlayerInfoData = new PlayerInfoData(playerInfoData.getProfile().withName(nameTag), playerInfoData.getLatency(), playerInfoData.getGameMode(), nameTag.equals(tabName) ? null : WrappedChatComponent.fromText(player.getPlayerListName()));
                        Logging.debug(SkinManager.class, "Post Namtatg: " + newPlayerInfoData.getProfile().getName());

                        Skin skin = getPersonalDisplayedSkin(player, event.getPlayer());
                        Logging.debug(SkinManager.class, "PLAYER ACTUAL NAME: " + player.getName());
                        Logging.debug(SkinManager.class, "SKINTEXTURE REPLACEMENT (MAY OR MAY NOT EXIST): " + skin);
                        if (skin != null) {
                            newPlayerInfoData.getProfile().getProperties().put(Skin.MULTIMAP_KEY, skin.toWrappedSignedProperty());
                        }
                    }
                    newPlayerInfoDatas.add(newPlayerInfoData);
                }
                event.getPacket().getPlayerInfoDataLists().writeSafely(0, newPlayerInfoDatas);
            }
        });

        PacketManager.onPacketEvent(PacketType.Play.Server.SCOREBOARD_TEAM, event -> {
            if (!event.isCancelled() && event.getPlayer() != null) {
                Collection<String> playerNames = event.getPacket().getSpecificModifier(Collection.class).readSafely(0);
                Logging.debug(SkinManager.class, "playerNames: " + playerNames);
                List<String> addedNames = new ArrayList<String>();
                for (String s : playerNames) {
                    Player player = Bukkit.getPlayerExact(s);
                    if (player != null) {
                        String nameTag = getNameTag(player);
                        if (!nameTag.equals(s))
                            addedNames.add(nameTag);
                        Logging.debug(SkinManager.class, "Player " + s + ", Nametag " + nameTag);
                    }
                }
                Logging.debug(SkinManager.class, "addedNames: " + addedNames);
                Set<String> finalNames = new HashSet<String>();
                finalNames.addAll(playerNames);
                finalNames.addAll(addedNames);
                Logging.debug(SkinManager.class, "finalNames: " + finalNames);
                event.getPacket().getSpecificModifier(Collection.class).writeSafely(0, finalNames);
                checkForTeamChanges();
            }
        });

        PacketManager.onPacketEvent(PacketType.Play.Server.SCOREBOARD_SCORE, event -> {
            StructureModifier<String> stringStructureModifier = event.getPacket().getStrings();
            String actualString = stringStructureModifier.read(0);
            Player player = actualString == null ? null : Bukkit.getPlayerExact(actualString);
            if (player != null) {
                stringStructureModifier.writeSafely(0, getNameTag(player));
                Logging.debug(SkinManager.class, "REPLACING SCORE IN NAME " + actualString);
            }
        });
    }

    private SkinManager() {}

    //Join/Leave Events

    static void onJoin(Player player) {
        nameTags.put(player, player.getName());
        getActualSkin(player);
        getNameTag(player);
        getTablistName(player);
    }

    static void onQuit(Player player) {
        actualSkins.remove(player);
        displayedSkins.remove(player);
        for (Player target : Bukkit.getOnlinePlayers()) {
            personalDisplayedSkins.remove(player, target);
            personalDisplayedSkins.remove(target, player);
        }
        personalDisplayedSkins.remove(player, player);
        nameTags.remove(player);
        spawnedPlayers.remove(player);
    }

    //Public Methods

    public static Skin getActualSkin(Player player) {
        Skin skin = actualSkins.get(player);
        if (skin == null) {
            skin = Skin.fromGameProfile(WrappedGameProfile.fromPlayer(player));
            Logging.debug(SkinManager.class, "SKINTEXTURE GIVEN BY PROTOCOLLIB FOR PLAYER " + player.getName() + " = " + skin);
            if (!skin.toString().equals("[]")) {
                actualSkins.put(player, skin);
                if (!displayedSkins.containsKey(player)) {
                    displayedSkins.put(player, skin);
                }
            }
        }
        Logging.debug(SkinManager.class, "ACTUALSKIN OF PLAYER " + player.getName() + " = " + skin);
        return skin;
    }

    public static Skin getDisplayedSkin(Player player) {
        return displayedSkins.get(player);
    }

    //skinTexture = null will reset the player's displayed skin to their actual skin
    public static void setDisplayedSkin(Player player, Skin skin) {
        Logging.debug(SkinManager.class, "SETTING DISPLAYED SKIN OF" + player.getName() + " TO " + skin);
        if (skin != null && !skin.toString().equals("[]"))
            displayedSkins.put(player, skin);
        else
            displayedSkins.put(player, getActualSkin(player));
        if (spawnedPlayers.contains(player)) {
            ArrayList<Player> targets = new ArrayList<>();
            targets.addAll(Bukkit.getOnlinePlayers());
            targets.removeAll(personalDisplayedSkins.row(player).keySet());
            specificallyRefreshPlayer(player, targets);
            if (!personalDisplayedSkins.contains(player, player))
                respawnPlayer(player);
        }
    }

    public static Skin getPersonalDisplayedSkin(Player player, Player target) {
        Skin result = personalDisplayedSkins.get(player, target);
        if (result == null) {
            result = getDisplayedSkin(player);
        }
        return result;
    }

    public static void setPersonalDisplayedSkin(Player player, Collection<Player> targets, Skin value) {
        for (Player target : targets) {
            if (value != null) {
                personalDisplayedSkins.put(player, target, value);
            } else {
                personalDisplayedSkins.remove(player, target);
            }
        }
        if (spawnedPlayers.contains(player)) {
            specificallyRefreshPlayer(player, targets);
            if (targets.contains(player)) {
                respawnPlayer(player);
            }
        }
    }

    public static void setDisplayedSkinExcluding(Player player, Collection<Player> excludes, Skin value) {
        Skin oldSkin = displayedSkins.get(player);
        for (Player exclude : excludes) {
            if (!personalDisplayedSkins.contains(player, exclude)) {
                if (oldSkin != null) {
                    personalDisplayedSkins.put(player, exclude, oldSkin);
                } else {
                    personalDisplayedSkins.remove(player, exclude);
                }
            }
        }
        setDisplayedSkin(player, value);
    }

    public static String getNameTag(Player player) {
        String nameTag = nameTags.get(player);
        if (nameTag == null) {
            nameTag = player.getName();
            nameTags.put(player, nameTag);
        }
        return nameTag;
    }

    //skinTexture = null will reset the player's nametag to their actual name
    public static void setNameTag(Player player, String nameTag) {
        if (nameTag != null && nameTag.length() > 16) {
            nameTag = nameTag.substring(0, 16); //Nametags can only be up to 16 chars in length
        }
        Logging.debug(SkinManager.class, "Setting nametag of " + player.getName() + " to " + nameTag);
        String oldNameTag = getNameTag(player);
        if (nameTag == null)
            nameTag = player.getName();
        Team team = player.getScoreboard() != null ? player.getScoreboard().getEntryTeam(player.getName()) : null;
        if (team != null) {
            team.removeEntry(player.getName());
            Scheduling.syncDelay(1, () -> team.addEntry(player.getName()));
        }
        Objective objective = player.getScoreboard() != null ? player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) : null;
        Score score = null;
        int actualScore = 0;
        if (objective != null) {
            score = objective.getScore(player.getName());
            actualScore = score.getScore();
            score.setScore(0);
        }
        nameTags.put(player, nameTag);
        refreshPlayer(player);
        updateTablistName(player);
        if (objective != null) {
            score.setScore(actualScore);
        }
        nameTags.forEach(new BiConsumer<Player, String>() {
            @Override
            public void accept(Player nameTagOwner, String s) {
                if (s.equals(oldNameTag)) {
                    Team team1 = nameTagOwner.getScoreboard() != null ? nameTagOwner.getScoreboard().getEntryTeam(nameTagOwner.getName()) : null;
                    if (team1 != null) {
                        team1.removeEntry(nameTagOwner.getName());
                        Scheduling.syncDelay(1, () -> team1.addEntry(nameTagOwner.getName()));
                    }

                }
            }
        });
    }


    public static String getTablistName(Player player) {
        String tablistName = tabNames.get(player);
        if (tablistName == null) {
            tablistName = player.getName();
            tabNames.put(player, tablistName);
        }
        return tablistName;
    }

    public static void setTablistName(Player player, String tablistName) {
        if (tablistName == null)
            tablistName = player.getName();
        tabNames.put(player, tablistName);
        updateTablistName(player);
    }

    //Private Methods

    private static void refreshPlayer(Player player) {
        specificallyRefreshPlayer(player, Bukkit.getOnlinePlayers());
    }

    private static void specificallyRefreshPlayer(Player player, Collection<? extends Player> targets) {
        Logging.debug(SkinManager.class, "Now hiding player " + player.getName());
        for (Player target : targets) {
            if (!target.equals(player)) {
                target.hidePlayer(player);
            }
        }
        Scheduling.syncDelay(1, () ->  {
            Logging.debug(SkinManager.class, "Now showing player " + player.getName());
            for (Player target : targets) {
                if (!target.equals(player)){
                    target.showPlayer(player);
                }
            }
        });
        //DO NOT REMOVE THE FOLLOWING CODE
        //It ensures that targets who are not currently tracking the player and thus will not receive a spawn packet
        //still have the tab hidden for them if necessary
        Scheduling.syncDelay(2, () -> {
            PacketContainer hidePacket = PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            for (Player target : targets) {
                if (!TablistManager.getTablistOfPlayer(target).isPlayerVisible(player)) {
                    PacketManager.sendPacket(hidePacket, SkinManager.class, target);
                }
            }
        });
    }

    private static void respawnPlayer(Player player) {
        PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER), SkinManager.class, player);
        PacketManager.sendPacket(PacketUtil.playerInfoPacket(player, EnumWrappers.PlayerInfoAction.ADD_PLAYER), SkinManager.class, player);

        Location playerLoc = new WorldLockedLocation(player.getLocation());
        Logging.debug(SkinManager.class, "playerLoc1 = " + playerLoc);
        try {
            Logging.debug(SkinManager.class, "DEDICATED_PLAYER_LIST_MOVE_TO_WORLD: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            Logging.debug(SkinManager.class, "NMS_SERVER: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            Logging.debug(SkinManager.class, "DEDICATED_PLAYER_LIST_MOVE_TO_WORLD: " + DEDICATED_PLAYER_LIST_MOVE_TO_WORLD);
            DEDICATED_PLAYER_LIST_MOVE_TO_WORLD.invoke(Reflection.NMS_SERVER, CRAFT_PLAYER_GET_HANDLE.invoke(player), convertDimension(player.getWorld().getEnvironment()), true, playerLoc, true);
        } catch (Exception e) {
            Logging.debug(SkinManager.class, "Failed to make player see his skin change: " + player.getName());
            Logging.reportException(SkinManager.class, e);
        }
    }

    private static void checkForTeamChanges() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = player.getScoreboard() != null ? player.getScoreboard().getEntryTeam(player.getName()) : null;
            String fullTablistName = team == null ? getTablistName(player) : team.getPrefix() + getTablistName(player) + team.getSuffix();
            if (!player.getPlayerListName().equals(fullTablistName)) {
                updateTablistName(player);
            }
        }
    }

    private static void updateTablistName(Player player) {
        Team team = player.getScoreboard() != null ? player.getScoreboard().getEntryTeam(player.getName()) : null;
        if (team == null)
            player.setPlayerListName(getTablistName(player));
        else
            player.setPlayerListName(team.getPrefix() + getTablistName(player) + team.getSuffix());
    }

    private static int convertDimension(World.Environment dimension) {
        switch (dimension) {
            case NETHER: return -1;
            case THE_END: return 1;
        }
        return 0;
    }
}
