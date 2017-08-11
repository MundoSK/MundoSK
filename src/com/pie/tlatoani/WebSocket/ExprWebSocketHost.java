package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import mundosk_libraries.java_websocket.WebSocket;
import org.bukkit.event.Event;

import java.net.InetSocketAddress;

/**
 * Created by Tlatoani on 5/6/17.
 */
public class ExprWebSocketHost extends SimpleExpression<String> {
    private Expression<WebSocket> webSocketExpr;
    private boolean local;

    @Override
    protected String[] get(Event event) {
        WebSocket webSocket = webSocketExpr.getSingle(event);
        InetSocketAddress socketAddress = local ? webSocket.getLocalSocketAddress() : webSocket.getRemoteSocketAddress();
        if (socketAddress == null) {
            return new String[0];
        }
        return new String[]{socketAddress.getHostName()};
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
        return (local ? "local" : "remote") + " host of " + webSocketExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        webSocketExpr = (Expression<WebSocket>) expressions[0];
        local = i == 0;
        return true;
    }
}
