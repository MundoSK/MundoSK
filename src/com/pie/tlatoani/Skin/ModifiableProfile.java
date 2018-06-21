package com.pie.tlatoani.Skin;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Scheduling;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Tlatoani on 1/20/18.
 */
public class ModifiableProfile {

    public final Player player;
    private Skin actualSkin;
    private Skin generalDisplayedSkin = null;
    private String generalNametag = null;
    private Map<Player, Specific> specificMap = new HashMap<>();

    public ModifiableProfile(Player player) {
        this.player = player;
        generalDisplayedSkin = getActualSkin();
        generalNametag = player.getName();
    }

    public Skin getActualSkin() {
        if (actualSkin == null) {
            actualSkin = Skin.fromGameProfile(WrappedGameProfile.fromPlayer(player));
            Logging.debug(this, "SKINTEXTURE GIVEN BY PROTOCOLLIB FOR PLAYER " + player.getName() + " = " + actualSkin);
            if (actualSkin.toString().equals("[]")) {
                actualSkin = null;
            }
        }
        return actualSkin;
    }

    public Skin getGeneralDisplayedSkin() {
        return generalDisplayedSkin;
    }

    public String getGeneralNametag() {
        return generalNametag;
    }

    public Specific getSpecificProfile(Player target) {
        if (target == null || !target.isOnline()) {
            throw new IllegalArgumentException("Target must be non-null and online: " + target);
        }
        return specificMap.computeIfAbsent(target, Specific::new);
    }

    void onQuit(Player left) {
        specificMap.remove(left);
    }

    //Nulls means the player's actual skin
    public void setGeneralDisplayedSkin(Skin value) {
        if (value == null) {
            value = getActualSkin();
        }
        generalDisplayedSkin = value;
        Bukkit
                .getOnlinePlayers()
                .stream()
                .map(this::getSpecificProfile)
                .filter(specific -> specific.displayedSkin == null)
                .forEach(Specific::changeDisplayedSkin);
    }

    //Null means the player's actual name
    public void setGeneralNametag(String value) {
        if (value == null) {
            value = player.getName();
        } else if (value.length() > 16) {
            value = value.substring(0, 16);
        }
        String oldValue = generalNametag;
        generalNametag = value;
        Bukkit
                .getOnlinePlayers()
                .stream()
                .map(this::getSpecificProfile)
                .filter(specific -> specific.nametag == null)
                .filter(specific -> !player.equals(specific.target))
                .forEach(specific -> specific.changeNametag(oldValue, generalNametag));
    }

    public void consistentlySetDisplayedSkin(Skin value) {
        if (value == null) {
            value = getActualSkin();
        }
        generalDisplayedSkin = value;
        Bukkit
                .getOnlinePlayers()
                .stream()
                .map(this::getSpecificProfile)
                .forEach(specific -> {
                    specific.displayedSkin = null;
                    specific.changeDisplayedSkin();
                });
    }

    public void consistentlySetNametag(String value) {
        if (value == null) {
            value = player.getName();
        } else if (value.length() > 16) {
            value = value.substring(0, 16);
        }
        String oldValue = generalNametag;
        generalNametag = value;
        Bukkit
                .getOnlinePlayers()
                .stream()
                .map(this::getSpecificProfile)
                .filter(specific -> !player.equals(specific.target))
                .forEach(specific -> {
                    String specificOldValue = specific.nametag == null ? oldValue : specific.nametag;
                    specific.nametag = null;
                    specific.changeNametag(specificOldValue, generalNametag);
                });
    }

    public class Specific {
        public final Player target;
        Skin displayedSkin = null;
        String nametag = null;

        private Specific(Player target) {
            this.target = target;
            if (player.equals(target)) {
                nametag = player.getName();
            }
        }

        private void changeDisplayedSkin() {
            ProfileManager.refreshPlayer(player, target);
        }

        private void changeNametag(String oldValue, String value) {
            if (player.equals(target)) {
                throw new UnsupportedOperationException("You can't change the nametag of a player for themselves!");
            }
            Logging.debug(this, "Setting nametag of " + player.getName() + " to " + value + " for " + target.getName());
            Scoreboard scoreboard = target.getScoreboard();
            Objective objective = null;
            Score score = null;
            int actualScore = 0;
            if (scoreboard != null) {
                Team team = scoreboard.getEntryTeam(player.getName());
                if (team != null) {
                    team.removeEntry(player.getName());
                    Scheduling.syncDelay(1, () -> team.addEntry(player.getName()));
                }
                objective = scoreboard.getObjective(DisplaySlot.BELOW_NAME);
                if (objective != null) {
                    score = objective.getScore(player.getName());
                    actualScore = score.getScore();
                    score.setScore(0);
                }
            }
            ProfileManager.refreshPlayer(player, target);
            if (scoreboard != null) {
                if (objective != null) {
                    score.setScore(actualScore);
                }
                for (ModifiableProfile profile : ProfileManager.profileMap.values()) {
                    Specific specific = profile.getSpecificProfile(target);
                    if (oldValue.equals(specific.getNametag())) {
                        Team team = scoreboard.getEntryTeam(profile.player.getName());
                        if (team != null) {
                            team.removeEntry(profile.player.getName());
                            Scheduling.syncDelay(1, () -> team.addEntry(profile.player.getName()));
                        }
                    }
                }
            }
        }

        public Skin getDisplayedSkin() {
            return displayedSkin == null ? generalDisplayedSkin : displayedSkin;
        }

        public String getNametag() {
            return nametag == null ? generalNametag : nametag;
        }

        //Null means reverting to the general displayed skin
        public void setDisplayedSkin(Skin value) {
            if (value == displayedSkin) {
                return;
            }
            this.displayedSkin = value;
            changeDisplayedSkin();
        }

        //Null means reverting to the general nametag
        public void setNametag(String value) {
            if (player.equals(target)) {
                return;
            }
            if (value != null && value.length() > 16) {
                value = value.substring(0, 16);
            }
            if (Objects.equals(value, nametag)) {
                return;
            }
            String oldValue = this.nametag;
            this.nametag = value;
            changeNametag(oldValue == null ? generalNametag : oldValue, value == null ? generalNametag : value);
        }
    }
}
