package com.pie.tlatoani.WebSocket;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.WebSocket.Events.WebSocketServerEvent;
import mundosk_libraries.org.java_websocket.WebSocket;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffWebSocketSendMessage extends Effect {
    private Expression<WebSocket> webSocketExpr;
    private Expression<String> messageExpr;

    @Override
    protected void execute(Event event) {
        String message = messageExpr.getSingle(event);
        if (webSocketExpr == null) {
            for (WebSocket webSocket : ((WebSocketServerEvent) event).getWebSocketServer().connections()) {
                webSocket.send(message);
            }
        } else {
            for (WebSocket webSocket : webSocketExpr.getArray(event)) {
                webSocket.send(message);
            }
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket send " + messageExpr + (webSocketExpr == null ? "" : " through " + webSocketExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        messageExpr = (Expression<String>) expressions[0];
        webSocketExpr = (Expression<WebSocket>) expressions[1];
        if (webSocketExpr == null) {
            for (Class<? extends Event> eventClass : ScriptLoader.getCurrentEvents()) {
                if (WebSocketServerEvent.class.isAssignableFrom(eventClass)) {
                    return true;
                }
            }
            Skript.error("'websocket send %string%' can only be used under 'websocket server'!");
            return false;
        }
        return false;
    }
}
