package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.framing.CloseFrame;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class EffCloseWebSocket extends Effect {
    private Expression<WebSocket> webSocketExpr;
    private Expression<String> messageExpr;

    @Override
    protected void execute(Event event) {
        if (messageExpr == null) {
            webSocketExpr.getSingle(event).close();
        } else {
            webSocketExpr.getSingle(event).close(CloseFrame.NORMAL, messageExpr.getSingle(event));
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "close websocket " + webSocketExpr + (messageExpr == null ? "" : " with message " + messageExpr);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        webSocketExpr = (Expression<WebSocket>) expressions[0];
        messageExpr = (Expression<String>) expressions[1];
        return true;
    }
}
