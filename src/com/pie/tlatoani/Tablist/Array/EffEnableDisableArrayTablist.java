package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Player.PlayerTablist;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffEnableDisableArrayTablist extends Effect {
    private TablistProvider tablistProvider;
    private boolean enable;
    private Optional<Expression<Number>> columns;
    private Optional<Expression<Number>> rows;
    private Optional<Expression<Skin>> iconExpression;

    @Override
    protected void execute(Event event) {
        if (enable) {
            int columns = this.columns.map(expression -> expression.getSingle(event).intValue()).orElse(4);
            int rows = this.rows.map(expression -> expression.getSingle(event).intValue()).orElse(20);
            Skin initialIcon = this.iconExpression.map(expression -> expression.getSingle(event)).orElse(null);
            for (Tablist tablist : tablistProvider.get(event)) {
                tablist.setSupplementaryTablist(playerTablist -> {
                    if (initialIcon != null) {
                        tablist.setDefaultIcon(initialIcon);
                    }
                    return new ArrayTablist(playerTablist, columns, rows);
                });
            }
        } else {
            for (Tablist tablist : tablistProvider.get(event)) {
                if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                    tablist.setSupplementaryTablist(SimpleTablist::new);
                    tablist.getPlayerTablist().ifPresent(PlayerTablist::showAllPlayers);
                }
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString((enable ? "enable" : "disable") + " array tablist [for %]" +
                ((enable && (columns.isPresent() || rows.isPresent() || iconExpression.isPresent())) ? " with"
                    + columns.map(expression -> " " + expression + " columns").orElse("")
                    + rows.map(expression -> " " + expression + " rows").orElse("")
                    + iconExpression.map(expression -> " initial icon " + expression).orElse("")
                : ""));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        enable = i == 0;
        if (enable) {
            columns = Optional.ofNullable((Expression<Number>) expressions[2]);
            rows = Optional.ofNullable((Expression<Number>) expressions[3]);
            iconExpression = Optional.ofNullable((Expression<Skin>) expressions[4]);
        }
        return true;
    }
}
