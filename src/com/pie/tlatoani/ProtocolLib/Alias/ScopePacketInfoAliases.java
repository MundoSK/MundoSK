package com.pie.tlatoani.ProtocolLib.Alias;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.SkriptLogger;
import com.comphenix.protocol.PacketType;
import com.pie.tlatoani.Util.GroupedList;
import com.pie.tlatoani.Util.MundoEventScope;
import com.pie.tlatoani.Util.ScopeUtil;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Tlatoani on 10/15/17.
 */
public class ScopePacketInfoAliases extends MundoEventScope {
    private PacketType packetType;
    private GroupedList.Key key;

    @Override
    protected void afterInit() {}

    @Override
    public void unregister(Trigger trigger) {
        ExprPacketInfoAlias.unregisterAliases(key);
    }

    @Override
    public void unregisterAll() {
        ExprPacketInfoAlias.unregisterAllAliases();
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        packetType = ((Literal<PacketType>) literals[0]).getSingle();
        SectionNode topNode = (SectionNode) SkriptLogger.getNode();
        try {
            List<PacketInfoAlias> aliases = new ArrayList<>();
            for (Node node : topNode) {
                if (node instanceof SectionNode) {
                    Skript.error("Packet info aliases should not be sections!");
                    return false;
                }
                int colonIndex = node.getKey().indexOf('=');
                if (colonIndex == -1) {
                    Skript.error("Packet info aliases should be in the format '<new syntax> = <old syntax>'");
                    return false;
                }
                String syntax = node.getKey().substring(0, colonIndex).trim();
                String original = node.getKey().substring(colonIndex + 1).trim();
                Optional<PacketInfoAlias> aliasOptional = PacketInfoAlias.create(packetType, syntax, original);
                if (!aliasOptional.isPresent()) {
                    Skript.error("Invalid target syntax in the packet info alias!");
                    return false;
                }
                aliases.add(aliasOptional.get());
            }
            key = ExprPacketInfoAlias.registerAliases(aliases);
            return true;
        } finally {
            ScopeUtil.removeSubNodes(topNode);
        }
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }
}
