package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import mundosk_libraries.java_websocket.WebSocket;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/11/17.
 */
public class ExprWebSocketState extends SimpleExpression<WebSocket.READYSTATE> {
    private Expression<WebSocket> webSocketExpression;

    @Override
    protected WebSocket.READYSTATE[] get(Event event) {
        return new WebSocket.READYSTATE[]{webSocketExpression.getSingle(event).getReadyState()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WebSocket.READYSTATE> getReturnType() {
        return WebSocket.READYSTATE.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return webSocketExpression + "'s websocket state";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        webSocketExpression = (Expression<WebSocket>) expressions[0];
        return true;
    }
}
