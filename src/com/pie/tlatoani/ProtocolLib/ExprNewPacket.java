package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class ExprNewPacket extends SimpleExpression<PacketContainer> {
    private Expression<PacketType> packetTypeExpression;

    @Override
    protected PacketContainer[] get(Event event) {
        return new PacketContainer[]{ProtocolLibrary.getProtocolManager().createPacket(packetTypeExpression.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends PacketContainer> getReturnType() {
        return PacketContainer.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "new %packettype% packet";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        packetTypeExpression = (Expression<PacketType>) expressions[0];
        return true;
    }
}
