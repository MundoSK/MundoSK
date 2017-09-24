package com.pie.tlatoani.ProtocolLib;

import com.comphenix.protocol.events.PacketContainer;

/**
 * Created by Tlatoani on 8/13/16.
 */
public abstract class PacketInfoConverter<T> {
    public final Class<? extends T> type;

    protected PacketInfoConverter() {
        this(null);
    }

    protected PacketInfoConverter(Class<? extends T> type) {
        this.type = type;
    }

    public abstract T get(PacketContainer packet, Integer index);

    public abstract void set(PacketContainer packet, Integer index, T value);
}
