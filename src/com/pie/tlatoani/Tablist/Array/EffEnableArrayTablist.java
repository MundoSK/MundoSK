package com.pie.tlatoani.Tablist.Array;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Simple.SimpleTablist;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.TablistManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffEnableArrayTablist extends Effect {
    private Expression<Player> playerExpression;
    private boolean enabled;
    private Optional<Expression<Number>> columns;
    private Optional<Expression<Number>> rows;
    private Optional<Expression<Skin>> iconExpression;

    @Override
    protected void execute(Event event) {
        if (enabled) {
            int columns = this.columns.map(expression -> expression.getSingle(event).intValue()).orElse(4);
            int rows = this.rows.map(expression -> expression.getSingle(event).intValue()).orElse(20);
            Skin initialIcon = this.iconExpression.map(expression -> expression.getSingle(event)).orElse(Tablist.DEFAULT_SKIN_TEXTURE);
            for (Player player : playerExpression.getArray(event)) {
                if (!player.isOnline()) {
                    continue;
                }
                TablistManager.getTablistOfPlayer(player).setSupplementaryTablist(playerTablist -> new ArrayTablist(playerTablist, columns, rows, initialIcon));
            }
        } else {
            for (Player player : playerExpression.getArray(event)) {
                if (!player.isOnline()) {
                    continue;
                }
                Tablist tablist = TablistManager.getTablistOfPlayer(player);
                if (tablist.getSupplementaryTablist() instanceof ArrayTablist) {
                    tablist.setSupplementaryTablist(SimpleTablist::new);
                }
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return (enabled ? "enable" : "disable") + " array tablist for " + playerExpression +
                ((enabled && (columns.isPresent() || rows.isPresent() || iconExpression.isPresent())) ? " with"
                    + columns.map(expression -> " " + expression + " columns").orElse("")
                    + rows.map(expression -> " " + expression + " rows").orElse("")
                    + iconExpression.map(expression -> " initial icon " + expression).orElse("")
                : "");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        playerExpression = (Expression<Player>) expressions[0];
        enabled = i == 1;
        if (enabled) {
            columns = Optional.ofNullable((Expression<Number>) expressions[1]);
            rows = Optional.ofNullable((Expression<Number>) expressions[2]);
            iconExpression = Optional.ofNullable((Expression<Skin>) expressions[3]);
        }
        return true;
    }
}
