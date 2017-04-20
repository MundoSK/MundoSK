package com.pie.tlatoani.Tablist.Simple;

import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tab;
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

    /*private final HashMap<String, String> displayNames = new HashMap<>();
    private final HashMap<String, Integer> latencies = new HashMap<>();
    private final HashMap<String, Skin> heads = new HashMap<>();
    private final HashMap<String, Integer> scores = new HashMap<>();*/

    private final HashMap<String, Tab> tabs = new HashMap<>();

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public SimpleTablist(Tablist.Storage storage) {
        this.tablist = storage.tablist;
        this.storage = storage;
    }

    //The following five methods no longer needed when SimpleTab

    /*
    private void sendPacketToAll(String id, EnumWrappers.PlayerInfoAction action) {
        sendPacket(id, action, tablist.players);
    }

    private void sendPacket(String id, EnumWrappers.PlayerInfoAction action, Collection<Player> players) {
        int ping = latencies.get(id);
        String displayName = displayNames.get(id);
        WrappedChatComponent chatComponent = WrappedChatComponent.fromJson(Tablist.colorStringToJson(displayName));
        UUID uuid = UUID.nameUUIDFromBytes(("MundoSKTablist::" + id).getBytes(UTF_8));
        Skin icon = heads.get(id);
        WrappedGameProfile gameProfile = new WrappedGameProfile(uuid, id + "-MSK");
        if (action == EnumWrappers.PlayerInfoAction.ADD_PLAYER) {
            if (icon == null) icon = Tablist.DEFAULT_SKIN_TEXTURE;
            icon.retrieveSkinTextures(gameProfile.getProperties());
        }
        PlayerInfoData playerInfoData = new PlayerInfoData(gameProfile, ping, EnumWrappers.NativeGameMode.NOT_SET, chatComponent);
        List<PlayerInfoData> playerInfoDatas = Arrays.asList(playerInfoData);
        PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        packetContainer.getPlayerInfoDataLists().writeSafely(0, playerInfoDatas);
        packetContainer.getPlayerInfoAction().writeSafely(0, action);
        for (Player player : players) {
            try {
                UtilPacketEvent.protocolManager.sendServerPacket(player, packetContainer);
            } catch (InvocationTargetException e) {
                Mundo.reportException(this, e);
            }
        }
    }

    private void sendScorePacketToAll(String id) {
        sendScorePacket(id, tablist.players);
    }

    private void sendScorePacket(String id, Collection<Player> players) {
        if (!tablist.areScoresEnabled()) return;
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
        packet.getStrings().writeSafely(0, id + "-MSK");
        packet.getStrings().writeSafely(1, Tablist.OBJECTIVE_NAME);
        packet.getIntegers().writeSafely(0, scores.get(id));
        packet.getScoreboardActions().writeSafely(0, EnumWrappers.ScoreboardAction.CHANGE);
        for (Player player : players) {
            try {
                UtilPacketEvent.protocolManager.sendServerPacket(player, packet);
            } catch (InvocationTargetException e) {
                Mundo.reportException(this, e);
            }
        }
    }


    public void addPlayers(Collection<Player> players) {
        for (String s : heads.keySet()) {
            sendPacket(s, EnumWrappers.PlayerInfoAction.ADD_PLAYER, players);
        }
    }*/

    //The following three methods have been modified to use SimpleTab, remove commented code later

    public void addPlayer(Player player) {
        for (Tab tab : tabs.values()) {
            if (!(tab instanceof SimplePersonalizable) || ((SimplePersonalizable) tab).isVisibleByDefault()) {
                tab.send(tab.showPacket(), player);
            }
        }
    }

    public void removePlayer(Player player) {
        /*for (String s : heads.keySet()) {
            sendPacket(s, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, Collections.singleton(player));
        }*/
        for (Tab tab : tabs.values()) {
            if (tab instanceof SimplePersonalizable) {
                ((SimplePersonalizable) tab).removePlayer(player);
            } else {
                tab.send(tab.hidePacket(), player);
            }
        }
    }

    /*
    public void clear(Player player) {
        String[] ids = displayNames.keySet().toArray(new String[0]);
        for (int i = 0; i < ids.length; i++) {
            deleteTab(ids[i]);
        }
    }
    */

    public boolean isEmpty() {
        return tabs.isEmpty();
    }

    public void clear() {
        for (Tab tab : tabs.values()) {
            tab.send(tab.hidePacket());
        }
        tabs.clear();
        storage.simpleTablistOptional = Optional.empty();
    }

    //These two methods no longer needed when SimpleTab

    /*
    public boolean tabExists(String id) {
        return id.length() <= 12 && displayNames.containsKey(id);
    }

    public void createTab(String id, String displayName, Integer ping, Skin head, Integer score) {
        tablist.arrayTablist.setColumns(0);
        if (id.length() <= 12 && !tabExists(id)) {
            ping = Math.max(ping, 0);
            ping = Math.min(ping, 5);
            latencies.put(id, ping);
            displayNames.put(id, displayName);
            heads.put(id, head);
            scores.put(id, score);
            sendPacketToAll(id, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
            if (score != 0) sendScorePacketToAll(id);
        }
    }*/

    public Tab createTab(String id, String displayName, Byte latency, Skin icon, Integer score) {
        if (id == null || id.length() > 12) {
            throw new IllegalArgumentException("Invalid id = " + id);
        }
        Tab tab = getTab(id);
        if (tab instanceof SimplePersonalizable) {
            ((SimplePersonalizable) tab).hideForAll();
            tab = new Tab(storage, id + "-MSK", UUID.nameUUIDFromBytes(("MundoSKTablist::" + id).getBytes(UTF_8)), displayName, latency, icon, score);
            tab.send(tab.showPacket());
            tabs.put(id, tab);
        } else {
            if (tab != null && icon == null && tab.getIcon() == null) {
                tab.setDisplayName(displayName);
                tab.setLatency(latency);
                tab.setScore(score);
            } else {
                tab = new Tab(storage, id + "-MSK", UUID.nameUUIDFromBytes(("MundoSKTablist::" + id).getBytes(UTF_8)), displayName, latency, icon, score);
                tab.send(tab.showPacket());
                tabs.put(id, tab);
            }
        }
        return tab;
    }

    public Tab getTab(String id) {
        return tabs.get(id);
    }

    public SimplePersonalizable forcePersonalizableTab(String id) {
        Tab tab = getTab(id);
        if (tab instanceof SimplePersonalizable) {
            return (SimplePersonalizable) tab;
        }
        SimplePersonalizable personalizableTab;
        if (tab == null) {
            personalizableTab = new SimplePersonalizable(this, id);
        } else {
            personalizableTab = new SimplePersonalizable(this, id, tab);
        }
        tabs.put(id, personalizableTab);
        return personalizableTab;
    }

    public void deleteTab(String id) {
        Tab tab = getTab(id);
        tab.send(tab.hidePacket());
        removeTab(id);
    }

    private void removeTab(String id) {
        tabs.remove(id);
        if (isEmpty()) {
            //tablist.clearSimpleTablist();
        }
    }

    //All following methods no longer needed when SimpleTab

    /*
    public void deleteTab(String id) {
        if (tabExists(id)) {
            sendPacketToAll(id, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            displayNames.remove(id);
            latencies.remove(id);
            heads.remove(id);
        }
    }

    public String getDisplayName(String id) {
        return displayNames.get(id);
    }

    public Integer getLatency(String id) {
        return latencies.get(id);
    }

    public Skin getHead(String id) {
        return heads.get(id);
    }

    public Integer getScore(String id) {
        return scores.get(id);
    }

    public void setDisplayName(String id, String displayName) {
        if (tabExists(id)) {
            displayNames.put(id, displayName);
            sendPacketToAll(id, EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME);
        }
    }

    public void setLatency(String id, Integer ping) {
        if (tabExists(id)) {
            latencies.put(id, ping);
            sendPacketToAll(id, EnumWrappers.PlayerInfoAction.UPDATE_LATENCY);
        }
    }

    public void setHead(String id, Skin icon) {
        if (tabExists(id)) {
            sendPacketToAll(id, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            heads.put(id, icon);
            sendPacketToAll(id, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
    }

    public void setScore(String id, Integer ping) {
        if (tabExists(id)) {
            scores.put(id, ping);
            sendScorePacketToAll(id);
        }
    }
    */

    //Tab Class Modifications

    public static class SimplePersonalizable extends Tab.Personalizable {
        public final String id;
        public final SimpleTablist simpleTablist;

        public SimplePersonalizable(SimpleTablist simpleTablist, String id) {
            super(simpleTablist.storage, id + "-MSK", UUID.nameUUIDFromBytes(("MundoSKTablist::" + id).getBytes(UTF_8)));
            this.id = id;
            this.simpleTablist = simpleTablist;
        }

        public SimplePersonalizable(SimpleTablist simpleTablist, String id, Tab prev) {
            super(prev);
            this.id = id;
            this.simpleTablist = simpleTablist;
        }

        @Override
        public void hideForAll() {
            super.hideForAll();
            simpleTablist.removeTab(id);
        }

        @Override
        public void hideFor(Player player) {
            super.hideFor(player);
            if (!visibleByDefault && isUniform()) {
                simpleTablist.removeTab(id);
            }
        }

        @Override
        public void removeIfApplicable(Personal personal) {
            super.removeIfApplicable(personal);
            if (isUniform()) {
                if (visibleByDefault) {
                    simpleTablist.tabs.put(id, new Tab(this));
                } else {
                    simpleTablist.removeTab(id);
                }
            }
        }

        @Override
        public void setDisplayName(String value) {
            super.setDisplayName(value);
            if (isUniform() && visibleByDefault) {
                simpleTablist.tabs.put(id, new Tab(this));
            }
        }

        @Override
        public void setLatency(Byte value) {
            super.setLatency(value);
            if (isUniform() && visibleByDefault) {
                simpleTablist.tabs.put(id, new Tab(this));
            }
        }

        @Override
        public void setScore(Integer value) {
            super.setScore(value);
            if (isUniform() && visibleByDefault) {
                simpleTablist.tabs.put(id, new Tab(this));
            }
        }

    }

}
