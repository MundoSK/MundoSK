package com.pie.tlatoani.Tablist;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Arrays;

/**
 * Created by Tlatoani on 11/25/16.
 */
public class CondScoresEnabled extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpression;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{MundoUtil.check(playerExpression, event, player ->
                player.isOnline() && TablistManager.getTablistOfPlayer(player).areScoresEnabled()
        , positive)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "scores are " + (positive ? "enabled" : "disabled") + " in " + playerExpression + "'s tablist";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        boolean enabled = positive == (boolean) delta[0];
        for (Player player : playerExpression.getArray(event)) {
            if (!player.isOnline()) {
                continue;
            }
            TablistManager.getTablistOfPlayer(player).setScoresEnabled(enabled);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Boolean.class);
        }
        return null;
    }
}
