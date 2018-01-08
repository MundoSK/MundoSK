package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class EffSendPacket extends Effect {
    private Expression<PacketContainer> packetExpression;
    private Expression<Player> playerExpression;

    @Override
    protected void execute(Event event) {
        for (PacketContainer packet : packetExpression.getArray(event)) {
            PacketManager.sendPacket(packet, this, playerExpression.getArray(event));
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return "send packet %packet% to %player%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        packetExpression = (Expression<PacketContainer>) expressions[i];
        playerExpression = (Expression<Player>) expressions[(i + 1) % 2];
        return true;
    }
}
