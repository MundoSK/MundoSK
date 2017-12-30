package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.WebSocket.Events.*;
import mundosk_libraries.java_websocket.WebSocket;
import mundosk_libraries.java_websocket.client.WebSocketClient;
import mundosk_libraries.java_websocket.drafts.Draft_17;
import mundosk_libraries.java_websocket.handshake.ClientHandshake;
import mundosk_libraries.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class SkriptWebSocketClient extends WebSocketClient {
    public final WebSocketClientFunctionality functionality;

    public SkriptWebSocketClient(WebSocketClientFunctionality functionality, URI serverURI) {
        super(serverURI);
        this.functionality = functionality;
    }

    public SkriptWebSocketClient(WebSocketClientFunctionality functionality, URI serverURI, Map<String, String> headers) {
        super(serverURI, new Draft_17(), headers, 0);
        this.functionality = functionality;
    }

    @Override
    public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) {
        functionality.onHandshake.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketHandshakeEvent.Client(this, request, response)));
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        functionality.onOpen.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketOpenEvent.Client(this, handshakedata)));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        functionality.onClose.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketCloseEvent(this, code, reason, remote)));
    }

    @Override
    public void onMessage(String message) {
        functionality.onMessage.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketMessageEvent(this, message)));
    }

    @Override
    public void onError(Exception ex) {
        functionality.onError.ifPresent(triggerItem -> TriggerItem.walk(triggerItem, new WebSocketErrorEvent(this, ex)));
    }

}
