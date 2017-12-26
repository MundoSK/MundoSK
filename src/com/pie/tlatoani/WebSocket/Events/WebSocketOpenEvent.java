package com.pie.tlatoani.WebSocket.Events;

import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import mundosk_libraries.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketOpenEvent extends WebSocketEvent {
    public final Handshakedata handshake;

    public WebSocketOpenEvent(WebSocket webSocket, Handshakedata handshake) {
        super(webSocket);
        this.handshake = handshake;
    }

    public static class Server extends WebSocketOpenEvent implements WebSocketServerEvent {
        public final WebSocketServer server;

        public Server(WebSocketServer server, WebSocket webSocket, Handshakedata handshake) {
            super(webSocket, handshake);
            this.server = server;
        }

        @Override
        public WebSocketServer getWebSocketServer() {
            return server;
        }
    }
}
