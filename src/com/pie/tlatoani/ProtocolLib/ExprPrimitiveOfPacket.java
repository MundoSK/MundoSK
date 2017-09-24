package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class ExprPrimitiveOfPacket extends SimpleExpression<Number> {
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private int mark;

    @Override
    protected Number[] get(Event event) {
        Number result = null;
        if (mark == 0) {
            result = packetContainerExpression.getSingle(event).getBytes().readSafely(index.getSingle(event).intValue());
        } else if (mark == 1) {
            result = packetContainerExpression.getSingle(event).getShorts().readSafely(index.getSingle(event).intValue());
        } else if (mark == 2) {
            result = packetContainerExpression.getSingle(event).getIntegers().readSafely(index.getSingle(event).intValue());
        } else if (mark == 3) {
            result = packetContainerExpression.getSingle(event).getLongs().readSafely(index.getSingle(event).intValue());
        } else if (mark == 4) {
            result = packetContainerExpression.getSingle(event).getFloat().readSafely(index.getSingle(event).intValue());
        } else if (mark == 5) {
            result = packetContainerExpression.getSingle(event).getDoubles().readSafely(index.getSingle(event).intValue());
        }
        return new Number[]{result};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "Number %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        index = (Expression<Number>) expressions[0];
        packetContainerExpression = (Expression<PacketContainer>) expressions[1];
        mark = parseResult.mark;
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        if (mark == 0) {
            packetContainerExpression.getSingle(event).getBytes().writeSafely(index.getSingle(event).intValue(), ((Number) delta[0]).byteValue());
        } else if (mark == 1) {
            packetContainerExpression.getSingle(event).getShorts().writeSafely(index.getSingle(event).intValue(), ((Number) delta[0]).shortValue());
        } else if (mark == 2) {
            packetContainerExpression.getSingle(event).getIntegers().writeSafely(index.getSingle(event).intValue(), ((Number) delta[0]).intValue());
        } else if (mark == 3) {
            packetContainerExpression.getSingle(event).getLongs().writeSafely(index.getSingle(event).intValue(), ((Number) delta[0]).longValue());
        } else if (mark == 4) {
            packetContainerExpression.getSingle(event).getFloat().writeSafely(index.getSingle(event).intValue(), ((Number) delta[0]).floatValue());
        } else if (mark == 5) {
            packetContainerExpression.getSingle(event).getDoubles().writeSafely(index.getSingle(event).intValue(), ((Number) delta[0]).doubleValue());
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Number.class);
        return null;
    }
}
