package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.EquivalentConverter;
import com.comphenix.protocol.wrappers.BukkitConverters;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 7/2/16.
 */
public class ExprEntityOfPacket extends SimpleExpression<Entity> {
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private Expression<World> worldExpression;
    private boolean isSingle;

    @Override
    protected Entity[] get(Event event) {
        PacketContainer packet = packetContainerExpression.getSingle(event);
        World world = worldExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        if (isSingle) {
            return new Entity[]{packet.getEntityModifier(world).readSafely(index)};
        } else {
            int[] entityIDs = packet.getIntegerArrays().readSafely(index);
            Entity[] result = new Entity[entityIDs.length];
            for (int i = 0; i < entityIDs.length; i++) {
                result[i] = ProtocolLibrary.getProtocolManager().getEntityFromID(world, entityIDs[i]);
            }
            return result;
        }
    }

    @Override
    public boolean isSingle() {
        return isSingle;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return worldExpression + " pentity " + (isSingle ? "" : "array ") + "pinfo " + index + " of " + packetContainerExpression;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        isSingle = i == 0;
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        worldExpression = (Expression<World>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        PacketContainer packet = packetContainerExpression.getSingle(event);
        World world = worldExpression.getSingle(event);
        int index = this.index.getSingle(event).intValue();
        if (isSingle) {
            packet.getEntityModifier(world).writeSafely(index, (Entity) delta[0]);
        } else {
            int[] entityIDs = new int[delta.length];
            for (int i = 0; i < delta.length; i++) {
                entityIDs[i] = ((Entity) delta[i]).getEntityId();
            }
            packet.getIntegerArrays().writeSafely(index, entityIDs);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return isSingle ? CollectionUtils.array(Entity.class) : CollectionUtils.array(Entity[].class);
        return null;
    }
}
