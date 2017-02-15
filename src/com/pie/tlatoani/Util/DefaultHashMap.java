package com.pie.tlatoani.Util;

import com.pie.tlatoani.Mundo;

import java.util.WeakHashMap;

/**
 * Created by Tlatoani on 2/6/17.
 */
public class DefaultHashMap<K, V> extends WeakHashMap<K, V> {
    private V defaultValue = null;

    @Override
    public V get(Object key) {
        if (key == null) {
            return defaultValue;
        }
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        if (key == null) {
            clear();
            V oldValue = defaultValue;
            defaultValue = value;
            return defaultValue;
        } else if (value != null) {
            return super.put(key, value);
        } else {
            return super.remove(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return  defaultValue == value || super.containsValue(value);
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public V getOrDefault(K key) {
        return Mundo.firstNotNull(get(key), defaultValue);
    }
}
