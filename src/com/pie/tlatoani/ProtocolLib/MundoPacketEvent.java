package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tlatoani on 4/29/16.
 */
public class MundoPacketEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();

    private PacketEvent packetEvent;

    private static List<PacketType> packetTypesListenedFor = new ArrayList<PacketType>();

    public static void addListener(PacketType[] packettypes) {
        List<PacketType> packetTypesToStartListeningFor = new ArrayList<PacketType>();
        for (int i = 0; i < packettypes.length; i++) {
            if (!packetTypesListenedFor.contains(packettypes[i])) {
                packetTypesListenedFor.add(packettypes[i]);
                packetTypesToStartListeningFor.add(packettypes[i]);
            }
        }
        if (!packetTypesToStartListeningFor.isEmpty()) {
            PacketType[] packetTypes = packetTypesToStartListeningFor.toArray(new PacketType[0]);
            PacketManager.onPacketEvent(packetTypes, packetEvent -> {
                MundoPacketEvent evt = new MundoPacketEvent(packetEvent);
                Bukkit.getServer().getPluginManager().callEvent(evt);
            });
        }
    }

    public MundoPacketEvent(PacketEvent event) {
        this.packetEvent = event;
    }

    public PacketEvent getPacketEvent() {
        return this.packetEvent;
    }

    public PacketType getPacketType() {
        return packetEvent.getPacketType();
    }

    public PacketContainer getPacket() {
        return packetEvent.getPacket();
    }

    public Player getPlayer() {
        return packetEvent.getPlayer();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return packetEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean b) {
        packetEvent.setCancelled(b);
    }
}
