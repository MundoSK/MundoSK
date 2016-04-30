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
public class ExprStringArrayOfPacket extends SimpleExpression<String> {
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;

    @Override
    protected String[] get(Event event) {
        return packetContainerExpression.getSingle(event).getStringArrays().read(index.getSingle(event).intValue());
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "strings %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        index = (Expression<Number>) expressions[0];
        packetContainerExpression = (Expression<PacketContainer>) expressions[1];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        packetContainerExpression.getSingle(event).getStringArrays().write(index.getSingle(event).intValue(), (String[]) delta);
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(String[].class);
        return null;
    }
}