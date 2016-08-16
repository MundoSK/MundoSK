package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
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

    @Override
    protected Entity[] get(Event event) {
        return new Entity[]{packetContainerExpression.getSingle(event).getEntityModifier(worldExpression.getSingle(event)).readSafely(index.getSingle(event).intValue())};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Entity> getReturnType() {
        return Entity.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "%world% pentity %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        worldExpression = (Expression<World>) expressions[0];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        packetContainerExpression.getSingle(event).getEntityModifier(worldExpression.getSingle(event)).writeSafely(index.getSingle(event).intValue(), (Entity) delta[0]);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Entity.class);
        return null;
    }
}
