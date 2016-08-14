package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.pie.tlatoani.Mundo;
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
public class UtilPacketEvent extends Event implements Cancellable{
    private static final HandlerList handlers = new HandlerList();
    public static ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private PacketEvent packetEvent;
    private PacketType packetType;
    private PacketContainer packet;
    private Player player;

    private static List<PacketType> listeners = new ArrayList<PacketType>();
    public static void addListener(PacketType[] packettypes) {
        List<PacketType> cleanlist = new ArrayList<PacketType>();
        for (int i = 0; i < packettypes.length; i++) {
            if (!listeners.contains(packettypes[i])) {
                listeners.add(packettypes[i]);
                cleanlist.add(packettypes[i]);
            }
        }
        if (!cleanlist.isEmpty()) {
            PacketType[] packettypearray = cleanlist.toArray(new PacketType[0]);
            protocolManager.addPacketListener(new PacketAdapter(Mundo.instance, ListenerPriority.NORMAL, packettypearray) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    UtilPacketEvent evt = new UtilPacketEvent(event);
                    Bukkit.getServer().getPluginManager().callEvent(evt);
                }

                @Override
                public void onPacketSending(PacketEvent event) {
                    UtilPacketEvent evt = new UtilPacketEvent(event);
                    Bukkit.getServer().getPluginManager().callEvent(evt);
                }
            });
        }
    }

    public UtilPacketEvent(PacketEvent event) {
        this.packetEvent = event;
        this.packetType = event.getPacketType();
        this.packet = event.getPacket();
        this.player = event.getPlayer();
    }

    public PacketEvent getPacketEvent() {
        return this.packetEvent;
    }

    public PacketType getPacketType() {
        return this.packetType;
    }

    public PacketContainer getPacket() {
        return this.packet;
    }

    public Player getPlayer() {
        return this.player;
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
