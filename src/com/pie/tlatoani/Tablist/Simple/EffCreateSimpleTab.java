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
public class EffCreateSimpleTab extends Effect {
    private Expression<String> idExpression;
    private TablistProvider tablistProvider;
    private Optional<Expression<SimpleTab.Location>> locationExpression;
    private Optional<Expression<String>> priorityExpression;
    private Expression<String> displayNameExpression;
    private Optional<Expression<Number>> latencyBarsExpression;
    private Optional<Expression<Skin>> iconExpression;
    private Optional<Expression<Number>> scoreExpression;

    @Override
    protected void execute(Event event) {
        String id = idExpression.getSingle(event);
        String displayName = this.displayNameExpression.getSingle(event);
        if (id == null || displayName == null) {
            return;
        }
        SimpleTab.Location location = locationExpression.flatMap(expr -> Optional.ofNullable(expr.getSingle(event))).orElse(SimpleTab.Location.WITHIN_PLAYERS);
        String priority = priorityExpression.flatMap(expr -> Optional.ofNullable(expr.getSingle(event))).orElse(id);
        if (priority.length() > 12) {
            priority = priority.substring(0, 12);
        }
        Integer latencyBars = latencyBarsExpression.map(expression -> expression.getSingle(event)).map(Number::intValue).orElse(null);
        Skin icon = iconExpression.map(expression -> expression.getSingle(event)).orElse(null);
        Integer score = scoreExpression.map(expression -> expression.getSingle(event)).map(Number::intValue).orElse(null);
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.createTab(id, location, priority, displayName, latencyBars, icon, score);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("create simple tab " + idExpression
                + " [for %]" + locationExpression.map(expr -> " located " + expr).orElse("")
                + " with" + priorityExpression.map(expr -> " priority " + priorityExpression).orElse("")
                + " display name " + displayNameExpression
                + latencyBarsExpression.map(expr -> " latency bars " + expr).orElse("")
                + iconExpression.map(expr -> " icon " + expr).orElse("")
                + scoreExpression.map(expr -> " score " + expr).orElse(""));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        idExpression = (Expression<String>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        locationExpression = Optional.ofNullable((Expression<SimpleTab.Location>) expressions[3]);
        priorityExpression = Optional.ofNullable((Expression<String>) expressions[4]);
        displayNameExpression = (Expression<String>) expressions[5];
        latencyBarsExpression = Optional.ofNullable((Expression<Number>) expressions[6]);
        iconExpression = Optional.ofNullable((Expression<Skin>) expressions[7]);
        scoreExpression = Optional.ofNullable((Expression<Number>) expressions[8]);
        return true;
    }
}
