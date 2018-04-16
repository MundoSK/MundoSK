package com.pie.tlatoani.WebSocket.Events;

import com.pie.tlatoani.Util.Skript.BaseEvent;
import mundosk_libraries.java_websocket.WebSocket;

/**
 * Created by Tlatoani on 5/5/17.
 */
public abstract class WebSocketEvent extends BaseEvent {
    public final WebSocket webSocket;

    public WebSocketEvent(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
