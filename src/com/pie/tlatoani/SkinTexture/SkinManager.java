package com.pie.tlatoani.SkinTexture;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Tablist.TabListManager;
import com.pie.tlatoani.Util.UtilReflection;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 9/18/16.
 */
public class SkinManager {
    private static HashMap<UUID, SkinTexture> actualSkins = new HashMap<>();
    private static HashMap<UUID, SkinTexture> displayedSkins = new HashMap<>();
    private static HashMap<UUID, String> nameTags = new HashMap<>();

    private static ArrayList<UUID> spawnedPlayers = new ArrayList<>();

    //private static ArrayList<UUID> respawningPlayers = new ArrayList<>();

    private static boolean reflectionEnabled = false;
    private static UtilReflection.MethodInvoker craftPlayerGetHandle = null;
    private static UtilReflection.MethodInvoker moveToWorld = null;

    static {

        //Reflection stuff
        try {
            craftPlayerGetHandle = UtilReflection.getTypedMethod(UtilReflection.getCraftBukkitClass("entity.CraftPlayer"), "getHandle", UtilReflection.getMinecraftClass("EntityPlayer"));
            moveToWorld = UtilReflection.getMethod(UtilReflection.getMinecraftClass("DedicatedPlayerList"), "moveToWorld", UtilReflection.getMinecraftClass("EntityPlayer"), int.class, boolean.class, Location.class, boolean.class);
            reflectionEnabled = true;
        } catch (Exception e) {
            Mundo.reportException(SkinManager.class, e);
        }

        //Packet stuff

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!event.isCancelled()) {
                    if (event.getPacket().getPlayerInfoAction().read(0) == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
                        List<PlayerInfoData> playerInfoDatas = event.getPacket().getPlayerInfoDataLists().readSafely(0);
                        List<PlayerInfoData> newPlayerInfoDatas = new ArrayList<PlayerInfoData>();
                        for (PlayerInfoData playerInfoData : playerInfoDatas) {
                            Player player = Bukkit.getPlayer(playerInfoData.getProfile().getUUID());
                            PlayerInfoData newPlayerInfoData = playerInfoData;

                            if (!spawnedPlayers.contains(player.getUniqueId())) {
                                Mundo.debug(SkinManager.class, "NEW PLAYER !");
                                spawnedPlayers.add(player.getUniqueId());
                                if (!actualSkins.containsKey(playerInfoData.getProfile().getUUID()) && player != null) {
                                    if (!actualSkins.containsKey(player.getUniqueId())) {
                                        SkinTexture skinTexture = new SkinTexture.Collected(playerInfoData.getProfile().getProperties().get("textures"));
                                        Mundo.debug(SkinManager.class, "ALTERNATIVE SKINTEXTURE FOUND IN PACKET = " + skinTexture);
                                        if (!skinTexture.toString().equals("[]")) {
                                            actualSkins.put(playerInfoData.getProfile().getUUID(), skinTexture);
                                            displayedSkins.put(playerInfoData.getProfile().getUUID(), skinTexture);
                                        }
                                    }

                                }
                            }

                            if (player != null) {
                                Mundo.debug(SkinManager.class, "Pre Namtatg: " + playerInfoData.getProfile().getName());
                                //Team team = player.getScoreboard().getEntryTeam(player.getName());
                                //String nameTag = team == null ? getNameTag(player) : team.getPrefix() + getNameTag(player) + team.getSuffix();
                                String nameTag = getNameTag(player);
                                String tabName = player.getPlayerListName();
                                newPlayerInfoData = new PlayerInfoData(playerInfoData.getProfile().withName(nameTag), playerInfoData.getLatency(), playerInfoData.getGameMode(), nameTag.equals(tabName) ? null : WrappedChatComponent.fromText(player.getPlayerListName()));
                                Mundo.debug(SkinManager.class, "Post Namtatg: " + newPlayerInfoData.getProfile().getName());
                            }
                            newPlayerInfoDatas.add(newPlayerInfoData);

                            SkinTexture skinTexture = displayedSkins.get(newPlayerInfoData.getProfile().getUUID());
                            Mundo.debug(SkinManager.class, "PLAYER ACTUAL NAME: " + (player != null ? player.getName() : "NOT A REAL PLAYER"));
                            Mundo.debug(SkinManager.class, "SKINTEXTURE REPLACEMENT (MAY OR MAY NOT EXIST): " + skinTexture);
                            if (skinTexture != null) {
                                skinTexture.retrieveSkinTextures(newPlayerInfoData.getProfile().getProperties());
                            }
                        }
                        event.getPacket().getPlayerInfoDataLists().writeSafely(0, newPlayerInfoDatas);
                    }
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.SCOREBOARD_TEAM) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (!event.isCancelled()) {
                    Collection<String> playerNames = event.getPacket().getSpecificModifier(Collection.class).readSafely(0);
                    Mundo.debug(SkinManager.class, "playerNames: " + playerNames);
                    List<String> addedNames = new ArrayList<String>();
                    playerNames.forEach(new Consumer<String>() {
                        @Override
                        public void accept(String s) {
                            Player player = Bukkit.getPlayerExact(s);
                            if (player != null) {
                                String nameTag = getNameTag(player);
                                if (!nameTag.equals(s))
                                    addedNames.add(nameTag);

                                Mundo.debug(SkinManager.class, "Player " + s + ", Nametag " + nameTag);
                            }
                        }
                    });
                    Mundo.debug(SkinManager.class, "addedNames: " + addedNames);
                    Set<String> finalNames = new HashSet<String>();
                    finalNames.addAll(playerNames);
                    finalNames.addAll(addedNames);
                    Mundo.debug(SkinManager.class, "finalNames: " + finalNames);
                    event.getPacket().getSpecificModifier(Collection.class).writeSafely(0, finalNames);
                }
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.SCOREBOARD_SCORE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                StructureModifier<String> stringStructureModifier = event.getPacket().getStrings();
                String actualString = stringStructureModifier.read(0);
                Player player = actualString == null ? null : Bukkit.getPlayerExact(actualString);
                if (player != null) {
                    stringStructureModifier.writeSafely(0, getNameTag(player));
                    Mundo.debug(SkinManager.class, "REPLACING SCORE IN NAME " + actualString);
                }
            }
        });

    }

    private SkinManager() {}

    public static void onJoin(Player player) {
        nameTags.put(player.getUniqueId(), player.getName());
        getActualSkin(player);
    }

    public static void onQuit(Player player) {
        actualSkins.remove(player.getUniqueId());
        displayedSkins.remove(player.getUniqueId());
        nameTags.remove(player.getUniqueId());
    }

    public static SkinTexture getActualSkin(Player player) {
        SkinTexture skinTexture = actualSkins.get(player.getUniqueId());
        if (skinTexture == null) {
            skinTexture = new SkinTexture.Collected(WrappedGameProfile.fromPlayer(player).getProperties().get("textures"));
            Mundo.debug(SkinManager.class, "SKINTEXTURE GIVEN BY PROTOCOLLIB FOR PLAYER " + player.getName() + " = " + skinTexture);
            if (!skinTexture.toString().equals("[]")) {
                actualSkins.put(player.getUniqueId(), skinTexture);
                if (!displayedSkins.containsKey(player.getUniqueId())) {
                    displayedSkins.put(player.getUniqueId(), skinTexture);
                }
            }
        }
        Mundo.debug(SkinManager.class, "ACTUALSKIN OF PLAYER " + player.getName() + " = " + skinTexture);
        return skinTexture;
    }

    public static SkinTexture getDisplayedSkin(Player player) {
        return displayedSkins.get(player.getUniqueId());
    }

    //skinTexture = null will reset the player's displayed skin to their actual skin
    public static void setDisplayedSkin(Player player, SkinTexture skinTexture) {
        Mundo.debug(SkinManager.class, "SETTING DISPLAYED SKIN OF" + player.getName() + " TO " + skinTexture);
        if (skinTexture != null && !skinTexture.toString().equals("[]"))
            displayedSkins.put(player.getUniqueId(), skinTexture);
        else
            displayedSkins.put(player.getUniqueId(), getActualSkin(player));
        if (spawnedPlayers.contains(player.getUniqueId())) {
            refreshPlayer(player);
            //respawnPlayer(player);
            if (reflectionEnabled)
                respawnPlayerWithReflection(player);
        }
    }

    public static String getNameTag(Player player) {
        String nameTag = nameTags.get(player.getUniqueId());
        if (nameTag == null) {
            nameTag = player.getName();
            nameTags.put(player.getUniqueId(), nameTag);
        }
        return nameTag;
    }

    //skinTexture = null will reset the player's nametag to their actual name
    public static void setNameTag(Player player, String nameTag) {
        if (nameTag != null && nameTag.length() > 16) {
            nameTag = nameTag.substring(0, 16); //Nametags can only be up to 16 chars in length
        }
        Mundo.debug(SkinManager.class, "Setting nametag of " + player.getName() + " to " + nameTag);
        String oldNameTag = getNameTag(player);
        if (nameTag == null)
            nameTag = player.getName();
        Team team = player.getScoreboard() != null ? player.getScoreboard().getEntryTeam(player.getName()) : null;
        if (team != null) {
            team.removeEntry(player.getName());
            Mundo.scheduler.runTaskLater(Mundo.instance, new Runnable() {
                @Override
                public void run() {
                    team.addEntry(player.getName());
                }
            }, 1);
        }
        Objective objective = player.getScoreboard() != null ? player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) : null;
        Score score = null;
        int actualScore = 0;
        if (objective != null) {
            score = objective.getScore(player.getName());
            actualScore = score.getScore();
            score.setScore(0);
        }
        nameTags.put(player.getUniqueId(), nameTag);
        refreshPlayer(player);
        if (objective != null) {
            score.setScore(actualScore);
        }
    }

    private static void refreshPlayer(Player player) {
        Mundo.debug(SkinManager.class, "Now hiding player " + player.getName());
        UUID uuid = player.getUniqueId();
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getUniqueId().equals(uuid)) {
                target.hidePlayer(player);
            }
        }
        Mundo.scheduler.scheduleSyncDelayedTask(Mundo.instance, new Runnable() {
            @Override
            public void run() {

                Mundo.debug(SkinManager.class, "Now showing player " + player.getName());
                for (Player target : Bukkit.getOnlinePlayers()) {
                    if (!target.getUniqueId().equals(uuid)){
                        target.showPlayer(player);
                    }
                }
            }
        }, 1);
    }

    private static void respawnPlayerWithReflection(Player player) {
        boolean playerPrevHidden = TabListManager.playerIsHidden(player, player);
        if (!playerPrevHidden) TabListManager.hidePlayer(player, player);

        //Replace direct CraftBukkit accessing code with reflection
        //((org.bukkit.craftbukkit.v1_10_R1.CraftServer) Bukkit.getServer()).getHandle().moveToWorld(((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle(), ((CraftWorld) player.getWorld()).getHandle().dimension, true, player.getLocation(), true);
        moveToWorld.invoke(UtilReflection.nmsServer, craftPlayerGetHandle.invoke(player), convertDimension(player.getWorld().getEnvironment()), true, player.getLocation(), true);

        if (!playerPrevHidden) Mundo.scheduler.runTaskLater(Mundo.instance, new Runnable() {
            @Override
            public void run() {
                TabListManager.showPlayer(player, player);
            }
        }, 3);
    }

    private static int convertDimension(World.Environment dimension) {
        switch (dimension) {
            case NETHER: return -1;
            case THE_END: return 1;
        }
        return 0;
    }
}
