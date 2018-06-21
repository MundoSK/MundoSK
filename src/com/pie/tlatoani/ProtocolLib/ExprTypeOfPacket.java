package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.pie.tlatoani.Core.Skript.MundoPropertyExpression;

/**
 * Created by Tlatoani on 10/15/17.
 */
public class ExprTypeOfPacket extends MundoPropertyExpression<PacketContainer, PacketType> {
    @Override
    public PacketType convert(PacketContainer packetContainer) {
        return packetContainer.getType();
    }
}
