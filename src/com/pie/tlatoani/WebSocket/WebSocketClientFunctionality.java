package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketClientFunctionality {
    public final String id;

    public Optional<TriggerItem> onOpen = Optional.empty();
    public Optional<TriggerItem> onClose = Optional.empty();
    public Optional<TriggerItem> onMessage = Optional.empty();
    public Optional<TriggerItem> onError = Optional.empty();

    public WebSocketClientFunctionality() {
        this(null);
    }

    public WebSocketClientFunctionality(String id) {
        this.id = id;
    }

    public boolean isEmpty() {
        return !(
                onOpen.isPresent() ||
                onClose.isPresent() ||
                onMessage.isPresent() ||
                onError.isPresent()
        );
    }

    public void clear() {
        onOpen = Optional.empty();
        onClose = Optional.empty();
        onMessage = Optional.empty();
        onError = Optional.empty();
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
