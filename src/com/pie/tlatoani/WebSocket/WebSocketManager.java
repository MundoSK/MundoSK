package com.pie.tlatoani.WebSocket;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.DefaultExpression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import com.pie.tlatoani.Mundo;
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
        Mundo.registerType(WebSocket.class, "websocket").parser(new Mundo.SimpleParser<WebSocket>() {
            @Override
            public WebSocket parse(String s, ParseContext parseContext) {
                return null;
            }

            @Override
            public String toString(WebSocket webSocket, int flags) {
                return "websocket from host " + webSocket.getLocalSocketAddress().getHostName() + " port " + webSocket.getLocalSocketAddress().getPort()
                        + " to host " + webSocket.getRemoteSocketAddress().getHostName() + " port " + webSocket.getRemoteSocketAddress().getPort();
            }
        }).defaultExpression(new EventValueExpression<WebSocket>(WebSocket.class));
        Mundo.registerEffect(EffCloseWebSocket.class, "close websocket %websocket%");
        Mundo.registerEffect(EffWebSocketSendMessage.class, "websocket send %string% [through %-websockets%]");
        Mundo.registerEffect(EffStartWebSocketServer.class, "start websocket server %string% at port %number%");
        Mundo.registerEffect(EffStopWebSocketServer.class, "stop websocket server at port %number% [with timeout %-number%]");
        Mundo.registerEvent("WebSocket Client", ScopeWebSocketClient.class, WebSocketEvent.class, "websocket client %string%");
        Mundo.registerEvent("WebSocket Server", ScopeWebSocketServer.class, WebSocketEvent.class, "websocket server %string%");
        Mundo.registerEventValue(WebSocketEvent.class, WebSocket.class, event -> event.webSocket);
        Mundo.registerEventValue(WebSocketMessageEvent.class, String.class, event -> event.message);
        Mundo.registerEventValue(WebSocketErrorEvent.class, Throwable.class, event -> event.error);
        Mundo.registerExpression(ExprWebSocket.class, WebSocket.class, ExpressionType.COMBINED, "[new] websocket %string% connected to uri %string%");
        Mundo.registerExpression(ExprWebSocketID.class, String.class, ExpressionType.PROPERTY, "websocket id of %websocket%", "%websocket%'s websocket id");
        Mundo.registerExpression(ExprWebSocketServerPort.class, Number.class, ExpressionType.SIMPLE, "websocket [server] port");
        Mundo.registerExpression(ExprAllWebSockets.class, WebSocket.class, ExpressionType.PROPERTY, "all websockets [of server at port %-number%]");
        Mundo.registerExpression(ExprWebSocketServerID.class, String.class, ExpressionType.PROPERTY, "id of websocket server at port %number%");
        Mundo.registerExpression(ExprWebSocketHost.class, String.class, ExpressionType.PROPERTY, "local host of %websocket%", "(remote|external) host of %websocket%");
        Mundo.registerExpression(ExprWebSocketPort.class, Number.class, ExpressionType.PROPERTY, "local port of %websocket%", "(remote|external) port of %websocket%");
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
                Mundo.reportException(WebSocketManager.class, e);
            }
        }
    }

    public static void stopAllServers(int timeout) {
        servers.forEach((__, server) -> {
            try {
                server.stop(timeout);
            } catch (InterruptedException e) {
                Mundo.reportException(WebSocketManager.class, e);
            }
        });
        servers.clear();
    }
}
