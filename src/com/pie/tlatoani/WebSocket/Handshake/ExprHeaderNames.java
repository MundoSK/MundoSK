package com.pie.tlatoani.WebSocket.Handshake;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import org.bukkit.event.Event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class ExprHeaderNames extends SimpleExpression<String> {
    private Expression<Handshakedata> handshakeExpr;

    @Override
    protected String[] get(Event event) {
        Handshakedata handshake = handshakeExpr.getSingle(event);
        if (handshake == null) {
            return new String[0];
        }
        Iterator<String> headerNameIterator = handshake.iterateHttpFields();
        List<String> headerNames = new LinkedList<>();
        headerNameIterator.forEachRemaining(headerNames::add);
        return headerNames.toArray(new String[0]);
    }

    @Override
    public Iterator<String> iterator(Event event) {
        return handshakeExpr.getSingle(event).iterateHttpFields();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "all handshake header names of " + handshakeExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        handshakeExpr = (Expression<Handshakedata>) expressions[0];
        return true;
    }
}
