package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketServerFunctionality {
    public final String id;

    public TriggerItem onStart = null;
    public TriggerItem onStop = null;
    public TriggerItem onOpen = null;
    public TriggerItem onClose = null;
    public TriggerItem onMessage = null;
    public TriggerItem onError = null;

    public WebSocketServerFunctionality(String id) {
        this.id = id;
    }

    public void clear() {
        onStart = null;
        onStop = null;
        onOpen = null;
        onClose = null;
        onMessage = null;
        onError = null;
    }
}
