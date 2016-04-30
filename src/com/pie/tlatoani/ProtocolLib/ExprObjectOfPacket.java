package com.pie.tlatoani.ProtocolLib;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.pie.tlatoani.Mundo;
import org.bukkit.event.Event;

import java.lang.reflect.Field;

/**
 * Created by Tlatoani on 4/30/16.
 */
public class ExprObjectOfPacket extends SimpleExpression<Object> {
    private Expression<ClassInfo> classinfo;
    private Expression<Number> index;
    private Expression<PacketContainer> packetContainerExpression;
    private static Field structureModifier;

    static {
        try {
            structureModifier = PacketContainer.class.getDeclaredField("structureModifier");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object[] get(Event event) {
        Mundo.debug(this, "The class the expression refers to: " + classinfo.getSingle(event).getC());
        Object result = null;
        try {
            StructureModifier structureModifier = (StructureModifier) ExprObjectOfPacket.structureModifier.get(packetContainerExpression.getSingle(event));
            StructureModifier specificStructureModifier = structureModifier.withType(classinfo.getSingle(event).getC());
            result = specificStructureModifier.readSafely(index.getSingle(event).intValue());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return new Object[]{result};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Object> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "boolean %number% of %packet%";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        classinfo = (Expression<ClassInfo>) expressions[0];
        index = (Expression<Number>) expressions[1];
        packetContainerExpression = (Expression<PacketContainer>) expressions[2];
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode){
        try {
            StructureModifier structureModifier = (StructureModifier) ExprObjectOfPacket.structureModifier.get(packetContainerExpression.getSingle(event));
            StructureModifier specificStructureModifier = structureModifier.withType(classinfo.getSingle(event).getC());
            specificStructureModifier.writeSafely(index.getSingle(event).intValue(), delta[0]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) return CollectionUtils.array(Object.class);
        return null;
    }
}
