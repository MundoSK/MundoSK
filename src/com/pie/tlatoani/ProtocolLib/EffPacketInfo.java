package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Field;

/**
 * Created by Tlatoani on 4/15/17.
 */
public class EffPacketInfo extends Effect {
    private Expression<PacketContainer> packetExpression;

    @Override
    protected void execute(Event event) {
        PacketContainer packet = packetExpression.getSingle(event);
        Mundo.info("Packet Info Start");
        Mundo.info("PacketType = " + packet.getType());
        int i = 0;
        for (Object value : packet.getModifier().getValues()) {
            Mundo.info("Field " + i + ", Class = " + value.getClass() + ": " + value);
        }
        Mundo.info("Packet Info End");
    }

    @Override
    public String toString(Event event, boolean b) {
        return "packet info " + packetExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        packetExpression = (Expression<PacketContainer>) expressions[0];
        return true;
    }
}
