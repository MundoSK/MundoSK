package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketManager {
    private static Map<String, WebSocketClientFunctionality> clientFunctionalities = new HashMap<>();
    private static Map<String, WebSocketServerFunctionality> serverFunctionalities = new HashMap<>();
    private static Map<Integer, SkriptWebSocketServer> servers = new HashMap<>();

    public static WebSocketClientFunctionality getClientFunctionality(String id) {
        return clientFunctionalities.computeIfAbsent(id, __ -> new WebSocketClientFunctionality(id));
    }

    public static void clearClientFunctionalities() {
        clientFunctionalities.values().forEach(WebSocketClientFunctionality::clear);
    }

    public static WebSocketServerFunctionality getServerFunctionality(String id) {
        return serverFunctionalities.computeIfAbsent(id, __ -> new WebSocketServerFunctionality(id));
    }

    public static void clearServerFunctionalities() {
        serverFunctionalities.values().forEach(WebSocketServerFunctionality::clear);
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
}
