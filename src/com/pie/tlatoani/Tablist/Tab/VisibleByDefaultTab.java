package com.pie.tlatoani.Tablist.Tab;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.pie.tlatoani.Mundo;
import com.pie.tlatoani.Skin.Skin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

/**
 * Created by Tlatoani on 4/26/17.
 */
public class VisibleByDefaultTab extends BaseTab implements PersonalizableTab {
    protected HashMap<Player, Optional<PersonalTab>> personalTabs = new HashMap<>();

    public VisibleByDefaultTab(BaseTab base) {
        super(base);
    }

    @Override
    public Optional<? extends Tab> viewFor(Player player) {
        Optional<PersonalTab> personalOptional = personalTabs.get(player);
        if (personalOptional == null) {
            return Optional.of(this);
        }
        return personalOptional;
    }

    @Override
    public PersonalTab forceFor(Player player) {
        return Optional.ofNullable(personalTabs.get(player)).orElseGet(() -> {
            PersonalTab personal = new PersonalTab(this, player);
            personalTabs.put(player, Optional.of(personal));
            return Optional.of(personal);
        }).orElse(null);
    }

    @Override
    public boolean visibleFor(Player player) {
        Optional<PersonalTab> personalOptional = personalTabs.get(player);
        return personalOptional == null || personalOptional.isPresent();
    }

    @Override
    public void showFor(Player player) {
        Optional<PersonalTab> personalOptional = personalTabs.get(player);
        if (personalOptional != null && !personalOptional.isPresent()) {
            personalTabs.remove(player);
            send(showPacket(), player);
        }
    }

    @Override
    public PersonalTab showFor(Player player, String displayName, Byte latency, Skin icon) {
        Optional<PersonalTab> personalOptional = personalTabs.get(player);
        if (personalOptional != null && personalOptional.isPresent()) {
            PersonalTab personal = personalOptional.get();
            personal.setIcon(icon);
            return personal;
        } else {
            PersonalTab personal = new PersonalTab(this, player);
            if (personalOptional == null) {
                personal.send(personal.hidePacket());
            }
            personal.displayName = Optional.ofNullable(displayName);
            personal.latency = Optional.ofNullable(latency);
            personal.icon = Optional.ofNullable(icon);
            personal.send(personal.showPacket());
            personalTabs.put(player, Optional.of(personal));
            return personal;
        }
    }

    @Override
    public void hideFor(Player player) {
        Optional<PersonalTab> personalOptional = personalTabs.put(player, Optional.empty());
        if (personalOptional == null || personalOptional.isPresent()) {
            send(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null), player);
        }
    }

    public void setDisplayName(String value) {
        super.setDisplayName(value);
        personalTabs.values().removeIf(personalOptional ->
                Mundo.optionalCase(personalOptional, personal -> {
                    personal.displayName = null;
                    return personal.containsValue();
                }, () -> false)
        );
    }

    public void setLatency(Byte value) {
        super.setLatency(value);
        personalTabs.values().removeIf(personalOptional ->
                Mundo.optionalCase(personalOptional, personal -> {
                    personal.latency = null;
                    return personal.containsValue();
                }, () -> false)
        );
    }

    public void setIcon(Skin value) {
        super.icon = value;
        send(playerInfoPacket(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER, null, null, null));
        Mundo.sync(1, () -> {
            for (Player player : storage.players) {
                Optional<PersonalTab> personalOptional = personalTabs.get(player);
                if (personalOptional == null) {
                    send(showPacket(), player);
                } else {
                    personalOptional.ifPresent(tab -> {
                        send(tab.showPacket(), player);
                        if (!tab.containsValue()) {
                            personalTabs.remove(player);
                        }
                    });
                }
            }
        });
    }

    public void setScore(Integer value) {
        super.setScore(value);
        personalTabs.values().removeIf(personalOptional ->
                Mundo.optionalCase(personalOptional, personal -> {
                    personal.score = null;
                    return personal.containsValue();
                }, () -> false)
        );
    }
}
