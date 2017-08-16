package com.pie.tlatoani.TablistNew.Player;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.TablistNew.Simple.SimpleTablist;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 8/11/16.
 */
public class ExprPlayersAreVisible extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpression;

    @Override
    protected Boolean[] get(Event event) {
        return Arrays
                .stream(playerExpression.getArray(event))
                .map(player -> TablistManager.getTablistOfPlayer(player).arePlayersVisible())
                .toArray(Boolean[]::new);
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "players are visible in " + playerExpression + "'s tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Boolean visible = (Boolean) delta[0];
        if (visible) {
            for (Player player : playerExpression.getArray(event)) {
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                if (!tablist.arePlayersVisible() && !tablist.getPlayerTablist().isPresent()) {
                    tablist.setSupplementaryTablist(SimpleTablist::new);
                }
                tablist.getPlayerTablist().ifPresent(PlayerTablist::showAllPlayers);
            }
        } else {
            for (Player player : playerExpression.getArray(event)) {
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                if (tablist.arePlayersVisible() && !tablist.getPlayerTablist().isPresent()) {
                    tablist.setSupplementaryTablist(SimpleTablist::new);
                }
                tablist.getPlayerTablist().ifPresent(PlayerTablist::hideAllPlayers);
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
