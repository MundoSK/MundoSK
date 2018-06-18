package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/9/17.
 */
public class ExprWebSocketServerID extends SimpleExpression<String> {
    private Expression<Number> portExpr;

    @Override
    protected String[] get(Event event) {
        return Optional
                .ofNullable(portExpr.getSingle(event))
                .map(Number::intValue)
                .map(WebSocketManager::getServer)
                .map(server -> new String[]{server.functionality.id})
                .orElse(new String[0]);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "id of websocket server at port " + portExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        portExpr = (Expression<Number>) expressions[0];
        return true;
    }
}
