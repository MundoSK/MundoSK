package com.pie.tlatoani.WebSocket;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.WebSocket.Events.WebSocketServerEvent;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.server.WebSocketServer;
import org.bukkit.event.Event;

import java.util.Iterator;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class ExprAllWebSockets extends SimpleExpression<WebSocket> {
    private Expression<Number> portExpr;

    @Override
    protected WebSocket[] get(Event event) {
        WebSocketServer server = portExpr == null
                ? ((WebSocketServerEvent) event).getWebSocketServer()
                : WebSocketManager.getServer(portExpr.getSingle(event).intValue());
        return server.connections().toArray(new WebSocket[0]);
    }

    @Override
    public Iterator<WebSocket> iterator(Event event) {
        WebSocketServer server = portExpr == null
                ? ((WebSocketServerEvent) event).getWebSocketServer()
                : WebSocketManager.getServer(portExpr.getSingle(event).intValue());
        return server.connections().iterator();
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
        return "all websockets" + (portExpr == null ? "" : " of server at port " + portExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        portExpr = (Expression<Number>) expressions[0];
        if (portExpr == null) {
            for (Class<? extends Event> eventClass : ScriptLoader.getCurrentEvents()) {
                if (WebSocketServerEvent.class.isAssignableFrom(eventClass)) {
                    return true;
                }
            }
            Skript.error("'all websockets' can only be used under 'websocket server'!");
            return false;
        }
        return true;
    }
}
