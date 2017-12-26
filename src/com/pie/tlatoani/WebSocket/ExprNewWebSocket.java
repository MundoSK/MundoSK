package com.pie.tlatoani.WebSocket;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.Pair;
import com.pie.tlatoani.Util.Logging;
import mundosk_libraries.java_websocket.WebSocket;
import org.bukkit.event.Event;
import org.json.simple.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class ExprNewWebSocket extends SimpleExpression<WebSocket> {
    private Expression<String> idExpr;
    private Expression<String> uriExpr;
    private Expression<JSONObject> headersJSONExpr;
    private Variable headersVarExpr;

    @Override
    protected WebSocket[] get(Event event) {
        WebSocketClientFunctionality functionality = WebSocketManager.getClientFunctionality(idExpr.getSingle(event));
        URI uri;
        try {
            uri = new URI(uriExpr.getSingle(event));
        } catch (URISyntaxException e) {
            Logging.reportException(this, e);
            return null;
        }
        SkriptWebSocketClient webSocket;
        if (headersJSONExpr != null) {
            JSONObject headersJSON = headersJSONExpr.getSingle(event);
            Map<String, String> headers = new HashMap<>();
            headersJSON.forEach((key, value) -> {
                if (value instanceof String) {
                    headers.put((String) key, (String) value);
                }
            });
            webSocket = new SkriptWebSocketClient(functionality, uri, headers);
        } else if (headersVarExpr != null) {
            Iterator<Pair<String, Object>> headersVarIterator = headersVarExpr.variablesIterator(event);
            Map<String, String> headers = new HashMap<>();
            headersVarIterator.forEachRemaining(pair -> {
                if (pair.getValue() instanceof String) {
                    headers.put(pair.getKey(), (String) pair.getValue());
                }
            });
            webSocket = new SkriptWebSocketClient(functionality, uri, headers);
        } else {
            webSocket = new SkriptWebSocketClient(functionality, uri);
        }
        webSocket.connect();
        return new WebSocket[]{webSocket};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends WebSocket> getReturnType() {
        return SkriptWebSocketClient.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "websocket " + idExpr + " connected to uri " + uriExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        idExpr = (Expression<String>) expressions[0];
        uriExpr = (Expression<String>) expressions[1];
        headersJSONExpr = (Expression<JSONObject>) expressions[3];
        if (expressions[2] != null) {
            if (expressions[2] instanceof Variable && !expressions[2].isSingle()) {
                headersVarExpr = (Variable) expressions[2];
            } else {
                Skript.error("'" + expressions[2] + "' is not a list variable!");
                return false;
            }
        } else {
            expressions[2] = null;
        }
        return true;
    }
}
