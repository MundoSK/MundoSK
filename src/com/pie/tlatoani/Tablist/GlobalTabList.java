package com.pie.tlatoani.Tablist;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 7/8/16.
 */
public class GlobalTabList {
    private static HashMap<String, GlobalTabScore> globalScores = new HashMap<>();
    private static HashMap<String, PlayerTabList> playerTabLists = new HashMap<>();
    
    //PlayerTabLists

    public static boolean playerTabListExists(Player player) {
        return playerTabLists.containsKey(player.getUniqueId().toString());
    }

    public static void createPlayerTabList(Player player) {
        playerTabLists.put(player.getUniqueId().toString(), new PlayerTabList(player));
    }
    
    public static PlayerTabList getPlayerTabList(Player player) {
        return playerTabLists.get(player.getUniqueId().toString());
    }

    public static void deletePlayerTabList(Player player) {
        playerTabLists.remove(player.getUniqueId().toString());
    }
    
    //PlayerTabListsToBeUpdated

    public static void addUserToGlobalScore(String globalScoreName, String referral) {
        globalScores.get(globalScoreName).addUser(referral);
    }

    public static void removeUserFromGlobalScore(String globalScoreName, String referral) {
        globalScores.get(globalScoreName).removeUser(referral);
    }
    
    //GlobalScores
    
    private static class GlobalTabScore {
        private String displayName;
        private int ping;
        private Integer score;
        private ArrayList<String> users = new ArrayList<>();

        private GlobalTabScore(String displayName, int ping, Integer score) {
            this.displayName = displayName;
            this.ping = ping;
            this.score = score;
        }
        
        private void update() {
            users.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    GlobalTabList.playerTabLists.get(s).update();
                }
            });
        }

        private void addUser(String referral) {
            if (!users.contains(referral)) {
                users.add(referral);
            }
        }

        private void removeUser(String referral) {
            users.remove(referral);
        }
    }

    //

    public static boolean globalScoreExists(String name) {
        return globalScores.containsKey(name);
    }

    public static void createGlobalScore(String name, String displayName, int ping, Integer score) {
        GlobalTabScore globalTabScore = new GlobalTabScore(displayName, ping, score);
        globalScores.put(name, globalTabScore);
    }

    public static void deleteGlobalScore(String name) {
        GlobalTabScore tabScore = globalScores.get(name);
        globalScores.remove(name);
        tabScore.users.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                playerTabLists.get(s).removeTabScoreSafely(name, false);
            }
        });
    }

    //

    public static String getDisplayName(String name) {
        return globalScores.get(name).displayName;
    }

    public static void setDisplayName(String name, String displayName) {
        GlobalTabScore globalTabScore = globalScores.get(name);
        globalTabScore.displayName = displayName;
        globalTabScore.update();
    }

    public static int getPing(String name) {
        return globalScores.get(name).ping;
    }

    public static void setPing(String name, int ping) {
        GlobalTabScore globalTabScore = globalScores.get(name);
        if (ping >= 5) {
            globalTabScore.ping = 5;
        } else if (ping <= 1) {
            globalTabScore.ping = 1;
        } else {
            globalTabScore.ping = ping;
        }
        globalTabScore.update();
    }

    public static Integer getScore(String name) {
        return globalScores.get(name).score;
    }

    public static void setScore(String name, Integer score) {
        GlobalTabScore globalTabScore = globalScores.get(name);
        globalTabScore.score = score;
        globalTabScore.update();
    }

    //Player Quit
    @EventHandler
    public static void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String referral = player.getUniqueId().toString();
        globalScores.forEach(new BiConsumer<String, GlobalTabScore>() {
            @Override
            public void accept(String s, GlobalTabScore globalTabScore) {
                globalTabScore.removeUser(referral);
            }
        });
        playerTabLists.remove(referral);
    }
}
