package com.pie.tlatoani.Tablist.Simple;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.ProtocolLib.UtilPacketEvent;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.Tab.*;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.entity.Player;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class SimpleTablist {
    public final Tablist tablist;
    private final Tablist.Storage storage;

    private final HashMap<String, Tab> tabs = new HashMap<>();

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public SimpleTablist(Tablist.Storage storage) {
        this.tablist = storage.tablist;
        this.storage = storage;
    }

    public void clear() {
        for (Tab tab : tabs.values()) {
            tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
        }
        tabs.clear();
        storage.simpleTablistOptional = Optional.empty();
    }

    public Tab createTab(String id, String displayName, Integer latency, Skin icon, Integer score) {
        if (id == null || id.length() > 12) {
            throw new IllegalArgumentException("Invalid id = " + id);
        }
        return tabs.compute(id, (__, oldTab) -> {
            if (oldTab != null) {
                oldTab.sendPacket(oldTab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
            }
            Tab newTab = new Tab(storage, id + "-MSK", UUID.nameUUIDFromBytes(("MundoSKTablist::" + id).getBytes(UTF_8)), displayName, latency, icon, score);
            newTab.sendPacket(newTab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            return newTab;
        });
    }

    public Tab getTab(String id) {
        return tabs.get(id);
    }

    public void deleteTab(String id) {
        Tab tab = tabs.remove(id);
        if (tab != null) {
            tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
        }
    }
}
