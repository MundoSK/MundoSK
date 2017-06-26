package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketClientFunctionality {
    public final String id;

    public TriggerItem onOpen = null;
    public TriggerItem onClose = null;
    public TriggerItem onMessage = null;
    public TriggerItem onError = null;

    public WebSocketClientFunctionality(String id) {
        this.id = id;
    }

    public void clear() {
        onOpen = null;
        onClose = null;
        onMessage = null;
        onError = null;
    }

    public void debugTriggerItems() {
        Mundo.debug(this, "TriggerItems: " +
            onOpen + "," +
            onClose + "," +
            onMessage + "," +
            onError
        );
    }
}
