package com.pie.tlatoani.WebSocket.Handshake;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import mundosk_libraries.java_websocket.handshake.HandshakeBuilder;
import mundosk_libraries.java_websocket.handshake.Handshakedata;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 12/29/17.
 */
public class ExprContent extends SimpleExpression<Number> {
    private Expression<Handshakedata> handshakeExpr;

    @Override
    protected Number[] get(Event event) {
        Handshakedata handshake = handshakeExpr.getSingle(event);
        if (handshake == null || handshake.getContent() == null) {
            return new Number[0];
        }
        Number[] result = new Number[handshake.getContent().length];
        for (int i = 0; i < result.length; i++) {
            result[i] = handshake.getContent()[i];
        }
        return result;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "handshake content of " + handshakeExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        handshakeExpr = (Expression<Handshakedata>) expressions[0];
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Handshakedata handshake = handshakeExpr.getSingle(event);
        if (handshake instanceof HandshakeBuilder) {
            byte[] value = new byte[delta.length];
            for (int i = 0; i < value.length; i++) {
                value[i] = ((Number) delta[i]).byteValue();
            }
            ((HandshakeBuilder) handshake).setContent(value);
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Number[].class);
        return null;
    }
}
