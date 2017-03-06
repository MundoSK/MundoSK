package com.pie.tlatoani.Util;

import ch.njol.util.Pair;
import com.pie.tlatoani.Mundo;

import java.util.*;
import java.util.function.Supplier;

/**
 * Created by Tlatoani on 2/6/17.
 */
public class DefaultHashMap<K, V, M extends Map<K, V>> implements Map<K, V> {
    private V defaultValue = null;
    private final Supplier<M> mSupplier;
    private M map = null;

    public DefaultHashMap(Supplier<M> mSupplier) {
        this.mSupplier = mSupplier;
    }

    @Override
    public int size() {
        return (defaultValue == null ? 0 : 1) + (map == null ? 0 : map.size());
    }

    @Override
    public boolean isEmpty() {
        return defaultValue != null || (map != null && map.isEmpty());
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return defaultValue != null;
        } else {
            return map == null ? false : map.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        return defaultValue.equals(value) || (map != null && map.containsValue(value));
    }

    @Override
    public V get(Object key) {
        if (key == null) {
            return defaultValue;
        } else if (map != null) {
            return map.get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (value == null) {
            return remove(key);
        } else if (key == null) {
            V oldDefaultValue = defaultValue;
            defaultValue = value;
            map = null;
            return oldDefaultValue;
        } else {
            if (map == null) {
                map = mSupplier.get();
            }
            return map.put(key, value);
        }
    }

    @Override
    public V remove(Object key) {
        V oldValue = null;
        if (key == null) {
            oldValue = defaultValue;
            defaultValue = null;
        } else if (map != null) {
            oldValue = map.remove(key);
            if (map.isEmpty()) {
                map = null;
            }
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        defaultValue = null;
        map = null;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<K>(size());
        if (defaultValue != null) {
            keySet.add(null);
        }
        if (map != null) {
            keySet.addAll(map.keySet());
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<V>(size());
        if (defaultValue != null) {
            values.add(defaultValue);
        }
        if (map != null) {
            values.addAll(map.values());
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = new HashSet<>(size());
        if (defaultValue != null) {
            entrySet.add(new Pair<K, V>(null, defaultValue));
        }
        if (map != null) {
            entrySet.addAll(map.entrySet());
        }
        return entrySet;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public V getOrDefault(K key) {
        return getOrDefault(key, defaultValue);
    }

    /*@Override
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
    */
}
