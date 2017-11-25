package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/13/16.
 */
public class OldExprTypeOfPacket extends SimpleExpression<PacketType> {
    private Expression<PacketContainer> packetContainerExpression;

    @Override
    protected PacketType[] get(Event event) {
        return new PacketType[]{packetContainerExpression.getSingle(event).getType()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends PacketType> getReturnType() {
        return PacketType.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "packettype of " + packetContainerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        packetContainerExpression = (Expression<PacketContainer>) expressions[0];
        return true;
    }
}
