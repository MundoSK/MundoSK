package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import mundosk_libraries.java_websocket.WebSocket;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/9/17.
 */
public class ExprWebSocketID extends SimpleExpression<String> {
    private Expression<WebSocket> webSocketExpression;

    @Override
    protected String[] get(Event event) {
        WebSocket webSocket = webSocketExpression.getSingle(event);
        if (webSocket instanceof SkriptWebSocketClient) {
            return new String[]{((SkriptWebSocketClient) webSocket).functionality.id};
        }
        return new String[0];
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
        return "websocket id of " + webSocketExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        webSocketExpression = (Expression<WebSocket>) expressions[0];
        return true;
    }
}
