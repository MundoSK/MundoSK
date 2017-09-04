package com.pie.tlatoani.WebSocket;

import ch.njol.skript.classes.Comparator;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.util.Pair;
import com.pie.tlatoani.Util.Logging;
import com.pie.tlatoani.Util.Registration;
import com.pie.tlatoani.WebSocket.Events.WebSocketCloseEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketErrorEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketMessageEvent;
import mundosk_libraries.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketManager {
    private static Map<String, WebSocketClientFunctionality> clientFunctionalities = new HashMap<>();
    private static Map<String, WebSocketServerFunctionality> serverFunctionalities = new HashMap<>();
    private static Map<Integer, SkriptWebSocketServer> servers = new HashMap<>();
    
    public static void load() {

        Registration.registerType(WebSocket.class, "websocket")
                .defaultExpression(new EventValueExpression<>(WebSocket.class));
        Registration.registerEnum(WebSocket.READYSTATE.class, "websocketstate", WebSocket.READYSTATE.values(), new Pair<>("NOT YET CONNECTED", WebSocket.READYSTATE.NOT_YET_CONNECTED));

        Registration.registerComparator(WebSocket.class, WebSocket.READYSTATE.class, false,
                ((webSocket, readystate) -> Comparator.Relation.get(webSocket.getReadyState() == readystate)));

        Registration.registerEffect(EffCloseWebSocket.class, "close websocket %websocket% [with message %-string%]");
        Registration.registerEffect(EffWebSocketSendMessage.class, "websocket send %strings% [through %-websockets%]");
        Registration.registerEffect(EffStartWebSocketServer.class, "start websocket server %string% at port %number%");
        Registration.registerEffect(EffStopWebSocketServer.class, "stop websocket server at port %number% [with timeout %-number%]");

        Registration.registerEvent("WebSocket Client", ScopeWebSocketClient.class, WebSocketEvent.class, "websocket client %string%");
        Registration.registerEvent("WebSocket Server", ScopeWebSocketServer.class, WebSocketEvent.class, "websocket server %string%");

        Registration.registerEventValue(WebSocketEvent.class, WebSocket.class, event -> event.webSocket);
        Registration.registerEventValue(WebSocketMessageEvent.class, String.class, event -> event.message);
        Registration.registerEventValue(WebSocketErrorEvent.class, Throwable.class, event -> event.error);
        Registration.registerEventValue(WebSocketCloseEvent.class, Number.class, event -> event.code);
        Registration.registerEventValue(WebSocketCloseEvent.class, String.class, event -> event.reason);
        Registration.registerEventValue(WebSocketCloseEvent.class, Boolean.class, event -> event.remote);

        Registration.registerExpression(ExprWebSocket.class, WebSocket.class, ExpressionType.COMBINED, "[new] websocket %string% connected to uri %string%");
        Registration.registerExpression(ExprWebSocketServerPort.class, Number.class, ExpressionType.SIMPLE, "websocket [server] port");
        Registration.registerExpression(ExprAllWebSockets.class, WebSocket.class, ExpressionType.PROPERTY, "all websockets [of server at port %-number%]");
        Registration.registerExpression(ExprWebSocketServerID.class, String.class, ExpressionType.PROPERTY, "id of websocket server at port %number%");
        Registration.registerPropertyExpression(ExprWebSocketID.class, String.class, "websocket", "websocket id");
        Registration.registerPropertyExpression(ExprWebSocketHost.class, String.class, "websocket", "local host", "remote host", "external host");
        Registration.registerPropertyExpression(ExprWebSocketPort.class, Number.class, "websocket", "local port", "remote port", "external port");
        Registration.registerPropertyExpression(ExprWebSocketState.class, WebSocket.READYSTATE.class, "websocket", "websocket state");
    }

    public static WebSocketClientFunctionality getClientFunctionality(String id) {
        return clientFunctionalities.computeIfAbsent(id, __ -> new WebSocketClientFunctionality(id));
    }

    public static void clearClientFunctionalities() {
        clientFunctionalities.values().forEach(WebSocketClientFunctionality::unload);
    }

    public static WebSocketServerFunctionality getServerFunctionality(String id) {
        return serverFunctionalities.computeIfAbsent(id, __ -> new WebSocketServerFunctionality(id));
    }

    public static void clearServerFunctionalities() {
        serverFunctionalities.values().forEach(WebSocketServerFunctionality::unload);
    }

    public static SkriptWebSocketServer getServer(int port) {
        return servers.get(port);
    }

    public static SkriptWebSocketServer startServer(int port, String id) {
        SkriptWebSocketServer server = new SkriptWebSocketServer(getServerFunctionality(id), port);
        if (servers.computeIfAbsent(port, __ -> server) != server) {
            throw new IllegalArgumentException("There is already a WebSocketServer put at the port " + port);
        }
        server.start();
        return server;
    }

    public static void stopServer(int port, int timeout) {
        SkriptWebSocketServer server = servers.get(port);
        if (server != null) {
            try {
                server.stop(timeout);
                servers.remove(port);
            } catch (InterruptedException e) {
                Logging.reportException(WebSocketManager.class, e);
            }
        }
    }

    public static void stopAllServers(int timeout) {
        servers.forEach((__, server) -> {
            try {
                server.stop(timeout);
            } catch (InterruptedException e) {
                Logging.reportException(WebSocketManager.class, e);
            }
        });
        servers.clear();
    }
}
