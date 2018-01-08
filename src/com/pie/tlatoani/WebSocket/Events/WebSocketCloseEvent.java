package com.pie.tlatoani.WebSocket.Events;

import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketCloseEvent extends WebSocketEvent {
    public final int code;
    public final String reason;
    public final boolean remote;

    public WebSocketCloseEvent(WebSocket webSocket, int code, String reason, boolean remote) {
        super(webSocket);
        this.code = code;
        this.reason = reason;
        this.remote = remote;
    }

    public static class Server extends WebSocketCloseEvent implements WebSocketServerEvent {
        public final WebSocketServer server;

        public Server(WebSocketServer server, WebSocket webSocket, int code, String reason, boolean remote) {
            super(webSocket, code, reason, remote);
            this.server = server;
        }

        @Override
        public WebSocketServer getWebSocketServer() {
            return server;
        }
    }
}
