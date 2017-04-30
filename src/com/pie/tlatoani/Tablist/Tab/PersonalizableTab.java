package com.pie.tlatoani.Tablist.Tab;

import com.pie.tlatoani.Skin.Skin;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Tlatoani on 4/26/17.
 */
public interface PersonalizableTab extends Tab {

    Optional<? extends Tab> viewFor(Player player);

    PersonalTab forceFor(Player player);

    boolean visibleFor(Player player);

    void showFor(Player player);

    PersonalTab showFor(Player player, String displayName, Byte latency, Skin icon);

    void hideFor(Player player);
}
