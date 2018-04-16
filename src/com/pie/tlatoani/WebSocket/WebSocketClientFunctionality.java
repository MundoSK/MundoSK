package com.pie.tlatoani.WebSocket;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.TriggerItem;
import com.pie.tlatoani.Util.Skript.ScopeUtil;
import com.pie.tlatoani.WebSocket.Events.*;

import java.util.Optional;

/**
 * Created by Tlatoani on 5/4/17.
 */
public class WebSocketClientFunctionality {
    public final String id;

    private boolean loaded = false;

    public Optional<TriggerItem> onOpen = Optional.empty();
    public Optional<TriggerItem> onHandshake = Optional.empty();
    public Optional<TriggerItem> onClose = Optional.empty();
    public Optional<TriggerItem> onMessage = Optional.empty();
    public Optional<TriggerItem> onError = Optional.empty();

    public WebSocketClientFunctionality() {
        this(null);
    }

    public WebSocketClientFunctionality(String id) {
        this.id = id;
    }

    public static class Nebula {
        public Optional<SectionNode> onOpen = Optional.empty();
        public Optional<SectionNode> onHandshake = Optional.empty();
        public Optional<SectionNode> onClose = Optional.empty();
        public Optional<SectionNode> onMessage = Optional.empty();
        public Optional<SectionNode> onError = Optional.empty();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void load(Nebula nebula) {
        loaded = true;
        onOpen = nebula.onOpen.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketClientOpen", WebSocketOpenEvent.Client.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onHandshake = nebula.onHandshake.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketClientHandshake", WebSocketHandshakeEvent.Client.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onClose = nebula.onClose.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketClientClose", WebSocketCloseEvent.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onMessage = nebula.onMessage.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketClientMessage", WebSocketMessageEvent.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onError = nebula.onError.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketClientError", WebSocketErrorEvent.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
    }

    public void unload() {
        loaded = false;
        onOpen = Optional.empty();
        onHandshake = Optional.empty();
        onClose = Optional.empty();
        onMessage = Optional.empty();
        onError = Optional.empty();
    }

    public String toString() {
        return "WebSocketClientFunctionality(TriggerItems: "
                + String.join(", ", onOpen.toString(), onHandshake.toString(), onClose.toString(), onMessage.toString(), onError.toString())
                + ")";
    }
}
