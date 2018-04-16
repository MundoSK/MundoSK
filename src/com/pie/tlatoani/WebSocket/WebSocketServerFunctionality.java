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
public class WebSocketServerFunctionality {
    public final String id;

    private boolean loaded = false;

    public Optional<TriggerItem> onStart = Optional.empty();
    public Optional<TriggerItem> onStop = Optional.empty();
    public Optional<TriggerItem> onOpen = Optional.empty();
    public Optional<TriggerItem> onHandshake = Optional.empty();
    public Optional<TriggerItem> onClose = Optional.empty();
    public Optional<TriggerItem> onMessage = Optional.empty();
    public Optional<TriggerItem> onError = Optional.empty();

    public WebSocketServerFunctionality(String id) {
        this.id = id;
    }

    public static class Nebula {
        public Optional<SectionNode> onStart = Optional.empty();
        public Optional<SectionNode> onStop = Optional.empty();
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
        onStart = nebula.onStart.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketServerStart", WebSocketServerStartEvent.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onStop = nebula.onStop.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketServerStop", WebSocketServerStopEvent.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onOpen = nebula.onOpen.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketServerOpen", WebSocketOpenEvent.Server.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onHandshake = nebula.onHandshake.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketServerHandshake", WebSocketHandshakeEvent.Server.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onClose = nebula.onClose.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketServerClose", WebSocketCloseEvent.Server.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onMessage = nebula.onMessage.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketServerMessage", WebSocketMessageEvent.Server.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
        onError = nebula.onError.flatMap(sectionNode -> {
            ScriptLoader.setCurrentEvent("WebSocketServerError", WebSocketErrorEvent.Server.class);
            return ScopeUtil.loadSectionNode(sectionNode, null);
        });
    }

    public void unload() {
        loaded = false;
        onStart = Optional.empty();
        onStop = Optional.empty();
        onOpen = Optional.empty();
        onHandshake = Optional.empty();
        onClose = Optional.empty();
        onMessage = Optional.empty();
        onError = Optional.empty();
    }

    public String toString() {
        return "WebSocketServerFunctionality(TriggerItems: "
                + String.join(", ", onStart.toString(), onStop.toString(), onOpen.toString(), onHandshake.toString(), onClose.toString(), onMessage.toString(), onError.toString())
                + ")";
    }
}
