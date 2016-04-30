package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class EffSendPacket extends Effect {
    Expression<PacketContainer> packetContainerExpression;
    Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        PacketContainer packetContainer = packetContainerExpression.getSingle(event);
        try {
            UtilPacketEvent.protocolManager.sendServerPacket(playerExpression.getSingle(event), packetContainer);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot send packet " + packetContainer, e);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "send packet %packet% to %player%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        packetContainerExpression = (Expression<PacketContainer>) expressions[i];
        playerExpression = (Expression<Player>) expressions[(i + 1) % 2];
        return true;
    }
}
