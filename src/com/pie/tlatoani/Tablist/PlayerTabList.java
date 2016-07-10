package com.pie.tlatoani.Tablist;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tlatoani on 7/8/16.
 */
public class PlayerTabList {
    public final Player player;
    private ArrayList<TabScoreIdentifier> tabList = new ArrayList<>();
    private HashMap<String, PersonalTabScore> personalScores = new HashMap<>();

    PlayerTabList(Player player) {
        this.player = player;
    }

    public void update() {
        //This will be what makes everything work
    }

    public boolean hasTabScore(String name, boolean isPersonal) {
        return tabList.contains(new TabScoreIdentifier(isPersonal, name));
    }

    public int getTabScoreIndex(String name, boolean isPersonal) {
        return tabList.indexOf(new TabScoreIdentifier(isPersonal, name));
    }

    public TabScoreIdentifier getTabScore(int index) {
        return tabList.get(index);
    }

    public void addTabScore(String name, boolean isPersonal, int position) {
        if (tabScoreExists(name, isPersonal)) {
            removeTabScore(name, isPersonal);
            tabList.set(position, new TabScoreIdentifier(isPersonal, name));
            if (!isPersonal) {
                GlobalTabList.addUserToGlobalScore(name, player.getUniqueId().toString());
            }
        }
    }

    public void removeTabScore(String name, boolean isPersonal) {
        tabList.remove(new TabScoreIdentifier(isPersonal, name));
        if (!isPersonal) {
            GlobalTabList.removeUserFromGlobalScore(name, player.getUniqueId().toString());
        }
    }

    void removeTabScoreSafely(String name, boolean isPersonal) {
        tabList.remove(new TabScoreIdentifier(isPersonal, name));
    }

    //Util

    public boolean tabScoreExists(String name, boolean isPersonal) {
        return isPersonal ? personalScoreExists(name) : GlobalTabList.globalScoreExists(name);
    }

    //PersonalScores

    public boolean personalScoreExists(String name) {
        return personalScores.containsKey(name);
    }

    public void createPersonalScore(String name, String displayName, int ping, Integer score) {
        PersonalTabScore personalTabScore = new PersonalTabScore(displayName, ping, score);
        personalScores.put(name, personalTabScore);
    }

    public void deletePersonalScore(String name) {
        personalScores.remove(name);
        removeTabScore(name, true);
    }

    //

    public String getDisplayName(String name) {
        return personalScores.get(name).displayName;
    }

    public void setDisplayName(String name, String displayName) {
        personalScores.get(name).displayName = displayName;
        update();
    }

    public int getPing(String name) {
        return personalScores.get(name).ping;
    }

    public void setPing(String name, int ping) {
        if (ping >= 5) {
            personalScores.get(name).ping = 5;
        } else if (ping <= 1) {
            personalScores.get(name).ping = 1;
        } else {
            personalScores.get(name).ping = ping;
        }
        update();
    }

    public Integer getScore(String name) {
        return personalScores.get(name).score;
    }

    public void setScore(String name, Integer score) {
        personalScores.get(name).score = score;
        update();
    }

    private class PersonalTabScore {
        private String displayName;
        private int ping;
        private Integer score;

        private PersonalTabScore(String displayName, int ping, Integer score) {
            this.displayName = displayName;
            this.ping = ping;
            this.score = score;
        }
    }

    public static class TabScoreIdentifier {
        public final boolean isPersonal;
        public final String name;

        public TabScoreIdentifier(boolean isPersonal, String name) {
            this.isPersonal = isPersonal;
            this.name = name;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof TabScoreIdentifier) {
                TabScoreIdentifier casted = (TabScoreIdentifier) other;
                return isPersonal == casted.isPersonal && name.equals(casted.name);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (name.hashCode() * 2) + (isPersonal ? 1 : 0);
        }
    }
}
