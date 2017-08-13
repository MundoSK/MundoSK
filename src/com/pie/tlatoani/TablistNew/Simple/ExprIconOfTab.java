package com.pie.tlatoani.TablistNew.Simple;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.TablistNew.OldTab;
import com.pie.tlatoani.TablistNew.OldTablist;
import com.pie.tlatoani.TablistNew.Tablist;
import com.pie.tlatoani.TablistNew.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class ExprIconOfTab extends SimpleExpression<Skin> {
    private Expression<String> id;
    private Expression<Player> playerExpression;

    @Override
    protected Skin[] get(Event event) {
        String id = this.id.getSingle(event);
        Player[] players = playerExpression.getArray(event);
        Skin[] icons = new Skin[players.length];
        for (int i = 0; i < players.length; i++) {
            Tablist tablist = TablistManager.getTablistOfPlayer(players[i]);
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                icons[i] = Optional.ofNullable(simpleTablist.getTab(id)).map(tab -> tab.getIcon()).orElse(null);
            }
        }
        return icons;
    }

    @Override
    public boolean isSingle() {
        return playerExpression.isSingle();
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "icon of simple tab " + id + " for " + playerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        playerExpression = (Expression<Player>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String id = this.id.getSingle(event);
        Skin value = (Skin) delta[0];
        for (Player player : playerExpression.getArray(event)) {
            Tablist tablist = TablistManager.getTablistOfPlayer(player);
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                Optional.ofNullable(simpleTablist.getTab(id)).ifPresent(tab -> tab.setIcon(value));
            }
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
