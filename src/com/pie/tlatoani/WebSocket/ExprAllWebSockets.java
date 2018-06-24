package com.pie.tlatoani.WebSocket;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.EmptyIterator;
import com.pie.tlatoani.Core.Static.Utilities;
import com.pie.tlatoani.WebSocket.Events.WebSocketServerEvent;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.server.WebSocketServer;
import org.bukkit.event.Event;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class ExprAllWebSockets extends SimpleExpression<WebSocket> {
    private Optional<Expression<Number>> portExpr;

    @Override
    protected WebSocket[] get(Event event) {
        Optional<WebSocketServer> server = portExpr
                .map(expr -> Optional.ofNullable(expr.getSingle(event)))
                .map(port -> port.map(Number::intValue).<WebSocketServer>map(WebSocketManager::getServer))
                .orElse(Optional.of(((WebSocketServerEvent) event).getWebSocketServer()));
        return server.map(s -> s.connections().toArray(new WebSocket[0])).orElse(new WebSocket[0]);
    }

    @Override
    public Iterator<WebSocket> iterator(Event event) {
        Optional<WebSocketServer> server = portExpr
                .map(expr -> Optional.ofNullable(expr.getSingle(event)))
                .map(port -> port.map(Number::intValue).<WebSocketServer>map(WebSocketManager::getServer))
                .orElse(Optional.of(((WebSocketServerEvent) event).getWebSocketServer()));
        return server.map(WebSocketServer::connections).map(Collection::iterator).orElseGet(EmptyIterator::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends WebSocket> getReturnType() {
        return WebSocket.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all websockets" + portExpr.map(expr -> " of server at port " + expr).orElse("");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        portExpr = Optional.ofNullable((Expression<Number>) expressions[0]);
        if (!portExpr.isPresent()) {
            if (Utilities.isAssignableFromCurrentEvent(WebSocketServerEvent.class)) {
                return true;
            }
            Skript.error("'all websockets' can only be used under 'websocket server'!");
            return false;
        }
        return true;
    }
}
