package com.pie.tlatoani.Tablist.Simple;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.Tab;
import com.pie.tlatoani.Tablist.SupplementaryTablist;
import com.pie.tlatoani.Tablist.Tablist;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Tlatoani on 7/15/16.
 */
public class SimpleTablist implements SupplementaryTablist {
    public final Tablist tablist;
    private final PlayerTablist playerTablist;

    private final HashMap<String, Tab> tabs = new HashMap<>();

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public SimpleTablist(PlayerTablist playerTablist) {
        this.tablist = playerTablist.tablist;
        this.playerTablist = playerTablist;
    }

    public void clear() {
        for (Tab tab : tabs.values()) {
            tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
        }
        tabs.clear();
    }

    public Tab createTab(String id, String displayName, Integer latency, Skin icon, Integer score) {
        if (id == null || id.length() > 12) {
            throw new IllegalArgumentException("Invalid id = " + id);
        }
        return tabs.compute(id, (__, oldTab) -> {
            if (oldTab != null) {
                oldTab.sendPacket(oldTab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
            }
            Tab newTab = new Tab(tablist.target, id + "-MSK", UUID.nameUUIDFromBytes(("MundoSKTablist::" + id).getBytes(UTF_8)), displayName, latency, icon, score);
            newTab.sendPacket(newTab.playerInfoPacket(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
            return newTab;
        });
    }

    public Optional<Tab> getTab(String id) {
        return Optional.ofNullable(tabs.get(id));
    }

    public void deleteTab(String id) {
        Tab tab = tabs.remove(id);
        if (tab != null) {
            tab.sendPacket(tab.playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER));
        }
    }

    @Override
    public void disable() {
        clear();
    }

    @Override
    public boolean allowExternalPlayerTabModification() {
        return true;
    }
}
