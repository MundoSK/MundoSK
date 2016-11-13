package com.pie.tlatoani.SkinTexture;

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
import com.pie.tlatoani.Generator.SkriptGenerator;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Tablist.TabListManager;
import org.apache.logging.log4j.core.net.Protocol;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 9/18/16.
 */
public class SkinManager {
    private static HashMap<UUID, SkinTexture> actualSkins = new HashMap<>();
    private static HashMap<UUID, SkinTexture> displayedSkins = new HashMap<>();
    private static HashMap<UUID, String> nameTags = new HashMap<>();

    private static ArrayList<UUID> respawningPlayers = new ArrayList<>();

    static {
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
                            if (!actualSkins.containsKey(playerInfoData.getProfile().getUUID()) && player != null) {
                                Mundo.debug(SkinManager.class, "NEW PLAYER !");
                                if (!actualSkins.containsKey(player.getUniqueId())) {
                                    SkinTexture skinTexture = new SkinTexture.Collected(playerInfoData.getProfile().getProperties().get("textures"));
                                    Mundo.debug(SkinManager.class, "ALTERNATIVE SKINTEXTURE FOUND IN PACKET = " + skinTexture);
                                    if (!skinTexture.toString().equals("[]")) {
                                        actualSkins.put(playerInfoData.getProfile().getUUID(), skinTexture);
                                        displayedSkins.put(playerInfoData.getProfile().getUUID(), skinTexture);
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

                                String tabName = player.getPlayerListName();
                                if (!tabName.equals(s) && !tabName.equals(nameTag))
                                    addedNames.add(tabName);

                                Mundo.debug(SkinManager.class, "Player " + s + ", Nametag " + nameTag + ", Tabname " + tabName);
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

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, /*PacketType.Play.Server.POSITION,*/ PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                /*double x = event.getPacket().getDoubles().readSafely(0);
                double z = event.getPacket().getDoubles().readSafely(2);
                Mundo.debug(SkinManager.class, "x, z = " + x + ", " + z);
                if (x == SkriptGenerator.X_CODE && z == SkriptGenerator.Z_CODE) {
                    event.setCancelled(true);
                    Mundo.debug(SkinManager.class, "EVENT CANCELLITU");
                }*/
                if (respawningPlayers.contains(event.getPlayer().getUniqueId())) {
                    event.setCancelled(true);
                    Mundo.debug(SkinManager.class, "RESPAWNING PLAYER " + event.getPlayer().getName() + " PACKETTYPE " + event.getPacketType());
                }
                Mundo.debug(SkinManager.class, "NOT RESPAWNING PLAYER " + event.getPlayer().getName() + " PACKETTYPE " + event.getPacketType());
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Mundo.instance, PacketType.Play.Server.SCOREBOARD_SCORE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                StructureModifier<String> stringStructureModifier = event.getPacket().getStrings();
                String actualString = stringStructureModifier.read(0);
                if (actualString != null) {
                    stringStructureModifier.writeSafely(0, getNameTag(Bukkit.getPlayerExact(actualString)));
                    Mundo.debug(SkinManager.class, "REPLACING SCORE IN NAME " + actualString);
                }
            }
        });
    }

    private SkinManager() {}

    public static void onJoin(Player player) {
        nameTags.put(player.getUniqueId(), player.getName());

        SkinTexture skinTexture = new SkinTexture.Collected(WrappedGameProfile.fromPlayer(player).getProperties().get("textures"));
        Mundo.debug(SkinManager.class, "SKINTEXTURE GIVEN BY PROTOCOLLIB FOR PLAYER " + player.getName() + " = " + skinTexture);
        if (!skinTexture.toString().equals("[]")) {
            actualSkins.put(player.getUniqueId(), skinTexture);
            displayedSkins.put(player.getUniqueId(), skinTexture);
        }
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
            if (!skinTexture.toString().equals("[]")) {
                actualSkins.put(player.getUniqueId(), skinTexture);
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
        //refreshPlayer(player);
        respawnPlayer(player);
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
            target.hidePlayer(player);
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

    private static void respawnPlayer(Player player) {
        boolean playerPrevHidden = TabListManager.playerIsHidden(player, player);
        if (!playerPrevHidden) TabListManager.hidePlayer(player, player);
        PacketContainer respawn1 = new PacketContainer(PacketType.Play.Server.RESPAWN);
        PacketContainer respawn2 = new PacketContainer(PacketType.Play.Server.RESPAWN);
        //PacketContainer position = new PacketContainer(PacketType.Play.Server.POSITION);
        EnumWrappers.Difficulty difficulty = convertDifficulty(player.getWorld().getDifficulty());
        respawn1.getDifficulties().writeSafely(0, difficulty);
        respawn2.getDifficulties().writeSafely(0, difficulty);
        respawn1.getGameModes().writeSafely(0, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()));
        respawn2.getGameModes().writeSafely(0, EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()));
        respawn1.getWorldTypeModifier().writeSafely(0, player.getWorld().getWorldType());
        respawn2.getWorldTypeModifier().writeSafely(0, player.getWorld().getWorldType());
        World.Environment environment = player.getWorld().getEnvironment();
        respawn1.getIntegers().writeSafely(0, environment == World.Environment.NETHER ? 0 : -1);
        respawn2.getIntegers().writeSafely(0, environment == World.Environment.NORMAL ? 0 : environment == World.Environment.NETHER ? -1 : 1);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, respawn1);
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, respawn2);
        } catch (InvocationTargetException e) {
            Mundo.reportException(SkinManager.class, e);
        }
        Location playerLoc = player.getLocation();
        GameMode playerMode = player.getGameMode();
        //Location testLoc = player.getLocation();
        //testLoc.setX(testLoc.getX() + 10000);
        //player.teleport(testLoc);
        //player.teleport(new Location(player.getWorld(), playerLoc.getX() + 10000, -5, playerLoc.getZ() + 10000));
        respawningPlayers.add(player.getUniqueId());
        player.setGameMode(GameMode.CREATIVE);
        player.teleport(new Location(player.getWorld(), SkriptGenerator.X_CODE, -5, SkriptGenerator.Z_CODE));
        Mundo.scheduler.runTaskLater(Mundo.instance, new Runnable() {
            @Override
            public void run() {
                respawningPlayers.remove(player.getUniqueId());
                player.teleport(playerLoc);
                player.setGameMode(playerMode);
                if (!playerPrevHidden)
                    TabListManager.showPlayer(player, player);
            }
        }, 20);

    }

    private static EnumWrappers.Difficulty convertDifficulty(Difficulty difficulty) {
        switch (difficulty) {
            case PEACEFUL: return EnumWrappers.Difficulty.PEACEFUL;
            case EASY: return EnumWrappers.Difficulty.EASY;
            case NORMAL: return EnumWrappers.Difficulty.NORMAL;
            case HARD: return EnumWrappers.Difficulty.HARD;
        }
        return null;
    }
}
