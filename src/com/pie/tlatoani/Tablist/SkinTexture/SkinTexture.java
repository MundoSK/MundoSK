package com.pie.tlatoani.Tablist.SkinTexture;

import ch.njol.util.Pair;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;
import com.pie.tlatoani.Mundo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 8/3/16.
 */
public class SkinTexture {
    private final List<Pair<String, String>> textures;

    public SkinTexture(String value, String signature) {
        textures = Arrays.asList(new Pair<>(value, signature));
    }

    public SkinTexture(Collection<WrappedSignedProperty> properties) {
        Pair<String, String>[] pairs = new Pair[properties.size()];
        properties.forEach(new Consumer<WrappedSignedProperty>() {
            int i = 0;

            @Override
            public void accept(WrappedSignedProperty wrappedSignedProperty) {
                pairs[i] = new Pair<String, String>(wrappedSignedProperty.getValue(), wrappedSignedProperty.getSignature());
                i++;
            }
        });
        textures = Arrays.asList(pairs);
    }

    public SkinTexture(JSONArray jsonArray) {
        Pair<String, String>[] pairs = new Pair[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            pairs[i] = new Pair<String, String>((String) jsonObject.get("value"), (String) jsonObject.get("signature"));
        }
        textures = Arrays.asList(pairs);
    }

    public void retrieveSkinTextures(Multimap<String, WrappedSignedProperty> multimap) {
        multimap.removeAll("textures");
        textures.forEach(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(Pair<String, String> pair) {
                Mundo.debug(SkinTexture.class, "PAIR: " + pair.getFirst() + "=second=" + pair.getSecond());
                WrappedSignedProperty property = new WrappedSignedProperty("textures", pair.getFirst(), pair.getSecond());
                multimap.put("textures", property);
            }
        });
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        textures.forEach(new Consumer<Pair<String, String>>() {
            @Override
            public void accept(Pair<String, String> pair) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("value", pair.getFirst());
                jsonObject.put("signature", pair.getSecond());
                jsonArray.add(jsonObject);
            }
        });
        return jsonArray;
    }

    @Override
    public String toString() {
        return toJSONArray().toJSONString();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SkinTexture && toString().equals(other.toString());
    }
}
