package com.pie.tlatoani.SkinTexture;

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
import com.pie.tlatoani.Tablist.TabListManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Created by Tlatoani on 9/18/16.
 */
public class SkinManager {
    private static HashMap<UUID, SkinTexture> actualSkins = new HashMap<>();
    private static HashMap<UUID, SkinTexture> displayedSkins = new HashMap<>();
    private static HashMap<UUID, String> nameTags = new HashMap<>();

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
                                SkinTexture skinTexture = new SkinTexture.Collected(playerInfoData.getProfile().getProperties().get("textures"));
                                actualSkins.put(playerInfoData.getProfile().getUUID(), skinTexture);
                                displayedSkins.put(playerInfoData.getProfile().getUUID(), skinTexture);
                                nameTags.put(playerInfoData.getProfile().getUUID(), playerInfoData.getProfile().getName());
                            }

                            if (player != null)
                                Mundo.debug(SkinManager.class, "Pre Namtatg: " + playerInfoData.getProfile().getName());
                                Team team = player.getScoreboard().getEntryTeam(player.getName());
                                String nameTag = team == null ? getNameTag(player) : team.getPrefix() + getNameTag(player) + team.getSuffix();
                                newPlayerInfoData = new PlayerInfoData(playerInfoData.getProfile().withName(nameTag), playerInfoData.getLatency(), playerInfoData.getGameMode(), playerInfoData.getDisplayName());
                                Mundo.debug(SkinManager.class, "Post Namtatg: " + newPlayerInfoData.getProfile().getName());
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
    }

    private SkinManager() {}

    public static void onJoin(Player player) {

    }

    public static void onQuit(Player player) {
        actualSkins.remove(player.getUniqueId());
        displayedSkins.remove(player.getUniqueId());
        nameTags.remove(player.getUniqueId());
    }

    public static SkinTexture getActualSkin(Player player) {
        return actualSkins.get(player.getUniqueId());
    }

    public static SkinTexture getDisplayedSkin(Player player) {
        return displayedSkins.get(player.getUniqueId());
    }

    //skinTexture = null will reset the player's displayed skin to their actual skin
    public static void setDisplayedSkin(Player player, SkinTexture skinTexture) {
        Mundo.debug(SkinManager.class, "SKINTEXTURE: " + skinTexture);
        if (skinTexture != null)
            displayedSkins.put(player.getUniqueId(), skinTexture);
        else
            displayedSkins.put(player.getUniqueId(), getActualSkin(player));
        refreshPlayer(player);
    }

    public static String getNameTag(Player player) {
        return nameTags.get(player.getUniqueId());
    }

    //skinTexture = null will reset the player's nametag to their actual name
    public static void setNameTag(Player player, String nameTag) {
        Mundo.debug(SkinManager.class, "Setting nametag of " + player.getName() + " to " + nameTag);
        String oldNameTag = getNameTag(player);
        if (nameTag == null)
            nameTag = player.getName();
        nameTags.put(player.getUniqueId(), nameTag);
        refreshPlayer(player);
    }

    private static void refreshPlayer(Player player) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            target.hidePlayer(player);
            //target.showPlayer(player);
        }
        Mundo.scheduler.scheduleSyncDelayedTask(Mundo.instance, new Runnable() {
            @Override
            public void run() {
                for (Player target : Bukkit.getOnlinePlayers()) {
                    target.showPlayer(player);
                }
            }
        }, 1);
    }
}
