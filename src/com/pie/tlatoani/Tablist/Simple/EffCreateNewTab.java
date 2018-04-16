package com.pie.tlatoani.Tablist.Simple;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Tablist;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class EffCreateNewTab extends Effect {
    private Expression<String> id;
    private TablistProvider tablistProvider;
    private Expression<String> displayName;
    private Optional<Expression<Number>> ping;
    private Optional<Expression<Skin>> iconExpression;
    private Optional<Expression<Number>> score;

    @Override
    protected void execute(Event event) {
        String id = this.id.getSingle(event);
        String displayName = this.displayName.getSingle(event);
        Integer latency = ping.map(expression -> expression.getSingle(event).intValue()).orElse(5);
        Skin icon = iconExpression.map(expression -> expression.getSingle(event)).orElse(Tablist.DEFAULT_SKIN_TEXTURE);
        Integer score = this.score.map(expression -> expression.getSingle(event).intValue()).orElse(0);
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.createTab(id, displayName, latency, icon, score);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "create simple tab " + id + " for " + tablistProvider + " with display name " + displayName + (ping == null ? "" : " latency " + ping) + (iconExpression == null ? "" : " icon " + iconExpression);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        id = (Expression<String>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        displayName = (Expression<String>) expressions[3];
        ping = Optional.ofNullable((Expression<Number>) expressions[4]);
        iconExpression = Optional.ofNullable((Expression<Skin>) expressions[5]);
        score = Optional.ofNullable((Expression<Number>) expressions[6]);
        return true;
    }
}
