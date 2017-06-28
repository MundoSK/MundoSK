package com.pie.tlatoani.WebSocket;

import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Mundo;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketServerFunctionality {
    public final String id;

    public Optional<TriggerItem> onStart = Optional.empty();
    public Optional<TriggerItem> onStop = Optional.empty();
    public Optional<TriggerItem> onOpen = Optional.empty();
    public Optional<TriggerItem> onClose = Optional.empty();
    public Optional<TriggerItem> onMessage = Optional.empty();
    public Optional<TriggerItem> onError = Optional.empty();

    public WebSocketServerFunctionality(String id) {
        this.id = id;
    }

    public boolean isEmpty() {
        return !(
                onStart.isPresent() ||
                onStop.isPresent() ||
                onOpen.isPresent() ||
                onClose.isPresent() ||
                onMessage.isPresent() ||
                onError.isPresent()
        );
    }

    public void clear() {
        onStart = Optional.empty();
        onStop = Optional.empty();
        onOpen = Optional.empty();
        onClose = Optional.empty();
        onMessage = Optional.empty();
        onError = null;
    }

    public void debugTriggerItems() {
        Mundo.debug(this, "TriggerItems: " +
                onStart + "," +
                onStop + "," +
                onOpen + "," +
                onClose + "," +
                onMessage + "," +
                onError
        );
    }
}
