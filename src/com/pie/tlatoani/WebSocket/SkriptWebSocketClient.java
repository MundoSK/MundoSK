package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.WebSocket.Events.WebSocketCloseEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketErrorEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketMessageEvent;
import com.pie.tlatoani.WebSocket.Events.WebSocketOpenEvent;
import mundosk_libraries.java_websocket.client.WebSocketClient;
import mundosk_libraries.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by Tlatoani on 5/5/17.
 */
public class SkriptWebSocketClient extends WebSocketClient {
    public final WebSocketClientFunctionality functionality;

    public SkriptWebSocketClient(WebSocketClientFunctionality functionality, URI serverUri) {
        super(serverUri);
        this.functionality = functionality;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        TriggerItem.walk(functionality.onOpen, new WebSocketOpenEvent(this));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        TriggerItem.walk(functionality.onClose, new WebSocketCloseEvent(this));
    }

    @Override
    public void onMessage(String message) {
        TriggerItem.walk(functionality.onMessage, new WebSocketMessageEvent(this, message));
    }

    @Override
    public void onError(Exception ex) {
        TriggerItem.walk(functionality.onError, new WebSocketErrorEvent(this, ex));
    }

}
