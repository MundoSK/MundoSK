package com.pie.tlatoani.CustomEventNew;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Core.Static.Utilities;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 1/14/18.
 */
public class ExprSpecific extends SimpleExpression<Object> {
    private CustomEventInfo info;
    private CustomEventInfo.SpecificExpression specificExprInfo;
    private Expression<? extends SkriptCustomEvent> eventExpr;

    @Override
    protected Object[] get(Event event) {
        SkriptCustomEvent customEvent;
        if (eventExpr == null) {
            customEvent = (SkriptCustomEvent) event;
        } else {
            customEvent = eventExpr.getSingle(event);
        }
        return new Object[]{customEvent.specificExpressions[specificExprInfo.index]};
    }

    @Override
    public boolean isSingle() {
        return specificExprInfo.single;
    }

    @Override
    public Class<?> getReturnType() {
        return specificExprInfo.type.getC();
    }

    @Override
    public String toString(Event event, boolean b) {
        return specificExprInfo.name + (eventExpr == null ? "" : " of " + eventExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        //info = something(i / 2);
        specificExprInfo = info.specificExpressions.get(parseResult.mark);
        eventExpr = (Expression<? extends SkriptCustomEvent>) expressions[0];
        if (eventExpr == null && !Utilities.isAssignableFromCurrentEvent(info.eventClass)) {
            Skript.error("The '" + specificExprInfo.name + "' expression must be used in a " + info.name + " event");
            return false;
        }
        return true;
    }
}
