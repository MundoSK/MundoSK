package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Tablist.TablistManager;
import com.pie.tlatoani.Util.MundoUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/8/18.
 */
public class CondArrayEnabled extends SimpleExpression<Boolean> {
    private Expression<Player> playerExpr;
    private boolean positive;

    @Override
    protected Boolean[] get(Event event) {
        return new Boolean[]{MundoUtil.check(playerExpr, event, player ->
                player.isOnline() && TablistManager.getTablistOfPlayer(player).getSupplementaryTablist() instanceof ArrayTablist
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
        return "array tablist is " + (positive ? "enabled" : "disabled") + " for " + playerExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpr = (Expression<Player>) expressions[0];
        positive = parseResult.mark == 0;
        return true;
    }
}
