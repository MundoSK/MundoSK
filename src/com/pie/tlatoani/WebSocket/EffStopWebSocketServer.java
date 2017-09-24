package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffStopWebSocketServer extends Effect {
    private Expression<Number> portExpr;
    private Expression<Number> timeoutExpr;

    @Override
    protected void execute(Event event) {
        WebSocketManager.stopServer(portExpr.getSingle(event).intValue(), timeoutExpr == null ? 0 : timeoutExpr.getSingle(event).intValue());
    }

    @Override
    public String toString(Event event, boolean b) {
        return "stop websocket server at port " + portExpr + (timeoutExpr == null ? "" : " with timeout " + timeoutExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        portExpr = (Expression<Number>) expressions[0];
        timeoutExpr = (Expression<Number>) expressions[1];
        return true;
    }
}
