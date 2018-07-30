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
    private Optional<Expression<String>> priorityExpression;
    private Optional<Expression<String>> displayNameExpression;
    private Optional<Expression<Number>> latencyBarsExpression;
    private Optional<Expression<Skin>> iconExpression;
    private Optional<Expression<Number>> scoreExpression;

    @Override
    protected void execute(Event event) {
        String id = idExpression.getSingle(event);
        if (id == null) {
            return;
        }
        String priority = priorityExpression.map(expression -> expression.getSingle(event)).orElse(null);
        if (priority != null && priority.length() > 12) {
            priority = priority.substring(0, 12);
        }
        String displayName = displayNameExpression.map(expression -> expression.getSingle(event)).orElse(null);
        Integer latencyBars = latencyBarsExpression.map(expression -> expression.getSingle(event)).map(Number::intValue).orElse(null);
        Skin icon = iconExpression.map(expression -> expression.getSingle(event)).orElse(null);
        Integer score = scoreExpression.map(expression -> expression.getSingle(event)).map(Number::intValue).orElse(null);
        for (Tablist tablist : tablistProvider.get(event)) {
            if (tablist.getSupplementaryTablist() instanceof SimpleTablist) {
                SimpleTablist simpleTablist = (SimpleTablist) tablist.getSupplementaryTablist();
                simpleTablist.createTab(id, priority, displayName, latencyBars, icon, score);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("create simple tab " + idExpression + " [for %]" + " with"
                + priorityExpression.map(expr -> " priority " + priorityExpression).orElse("")
                + displayNameExpression.map(expr -> " display name " + expr).orElse("")
                + latencyBarsExpression.map(expr -> " latency bars " + expr).orElse("")
                + iconExpression.map(expr -> " icon " + expr).orElse("")
                + scoreExpression.map(expr -> " score " + expr).orElse(""));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        idExpression = (Expression<String>) expressions[0];
        tablistProvider = TablistProvider.of(expressions, 1);
        priorityExpression = Optional.ofNullable((Expression<String>) expressions[3]);
        displayNameExpression = Optional.ofNullable((Expression<String>) expressions[4]);
        latencyBarsExpression = Optional.ofNullable((Expression<Number>) expressions[5]);
        iconExpression = Optional.ofNullable((Expression<Skin>) expressions[6]);
        scoreExpression = Optional.ofNullable((Expression<Number>) expressions[7]);
        return true;
    }
}
