package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class ExprPrimitiveArrayOfPacket extends SimpleExpression<Number> {
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private Boolean ifint;

    @Override
    protected Number[] get(Event event) {
        if (ifint) {
            int[] ints = packetContainerExpression.getSingle(event).getIntegerArrays().readSafely(index.getSingle(event).intValue());
            if (ints == null) return new Number[0];
            Number[] result = new Number[ints.length];
            for (int i = 0; i < ints.length; i++) {
                result[i] = new Integer(ints[i]);
            }
            return result;
        } else {
            byte[] bytes = packetContainerExpression.getSingle(event).getByteArrays().readSafely(index.getSingle(event).intValue());
            if (bytes == null) return new Number[0];
            Number[] result = new Number[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                result[i] = new Byte(bytes[i]);
            }
            return result;
        }

    }

    @Override
    public Iterator<Number> iterator(Event event) {
        if (ifint) {
            int[] ints = packetContainerExpression.getSingle(event).getIntegerArrays().readSafely(index.getSingle(event).intValue());
            Number[] result = new Number[ints.length];
            for (int i = 0; i < ints.length; i++) {
                result[i] = new Integer(ints[i]);
            }
            return Arrays.asList(result).iterator();
        } else {
            byte[] bytes = packetContainerExpression.getSingle(event).getByteArrays().readSafely(index.getSingle(event).intValue());
            Number[] result = new Number[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                result[i] = new Byte(bytes[i]);
            }
            return Arrays.asList(result).iterator();
        }
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
        return "bytes %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        index = (Expression<Number>) expressions[0];
        packetContainerExpression = (Expression<PacketContainer>) expressions[1];
        ifint = parseResult.mark == 0;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (ifint) {
            int[] ints = new int[delta.length];
            for (int i = 0; i < ints.length; i++) {
                ints[i] = ((Number) delta[i]).intValue();
            }
            packetContainerExpression.getSingle(event).getIntegerArrays().writeSafely(index.getSingle(event).intValue(), ints);
        } else {
            byte[] bytes = new byte[delta.length];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = ((Number) delta[i]).byteValue();
            }
            packetContainerExpression.getSingle(event).getByteArrays().writeSafely(index.getSingle(event).intValue(), bytes);
        }

    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Number[].class);
        return null;
    }
}