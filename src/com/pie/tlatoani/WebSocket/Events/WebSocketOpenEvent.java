package com.pie.tlatoani.WebSocket.Events;

import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import mundosk_libraries.java_websocket.server.WebSocketServer;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class WebSocketOpenEvent {

    private WebSocketOpenEvent() {}

    public static class Client extends WebSocketEvent {
        public final Handshakedata response;

        public Client(WebSocket webSocket, Handshakedata response) {
            super(webSocket);
            this.response = response;
        }
    }

    public static class Server extends WebSocketEvent implements WebSocketServerEvent {
        public final WebSocketServer server;
        public final Handshakedata request;

        public Server(WebSocketServer server, WebSocket webSocket, Handshakedata request) {
            super(webSocket);
            this.server = server;
            this.request = request;
        }

        @Override
        public WebSocketServer getWebSocketServer() {
            return server;
        }
    }
}
