package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffStopWebSocketServer extends Effect {
    private Expression<Number> portExpr;
    private Optional<Expression<Number>> timeoutExpr;

    @Override
    protected void execute(Event event) {
        Integer port = Optional.ofNullable(portExpr.getSingle(event)).map(Number::intValue).orElse(null);
        int timeout = timeoutExpr.map(expr -> expr.getSingle(event)).map(Number::intValue).orElse(0);
        if (port != null) {
            WebSocketManager.stopServer(portExpr.getSingle(event).intValue(), timeout);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "stop websocket server at port " + portExpr + timeoutExpr.map(expr -> " with timeout" + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        portExpr = (Expression<Number>) expressions[0];
        timeoutExpr = Optional.ofNullable((Expression<Number>) expressions[1]);
        return true;
    }
}
