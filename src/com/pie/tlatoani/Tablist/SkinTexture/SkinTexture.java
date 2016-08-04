package com.pie.tlatoani.Tablist.SkinTexture;

import ch.njol.util.Pair;
import ch.njol.util.coll.CollectionUtils;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;
import com.pie.tlatoani.Mundo;
import org.bukkit.entity.Player;
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
    private List<Pair<String, String>> textures;
    private Collection<WrappedSignedProperty> altTextures;

    public SkinTexture(String value, String signature) {
        textures = Arrays.asList(new Pair<>(value, signature));
    }

    public SkinTexture(Player player) {
        textures = null;
        altTextures = WrappedGameProfile.fromPlayer(player).getProperties().get("textures");
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
        if (textures != null) {
            textures.forEach(new Consumer<Pair<String, String>>() {
                @Override
                public void accept(Pair<String, String> pair) {
                    Mundo.debug(SkinTexture.class, "PAIR: " + pair.getFirst() + "=second=" + pair.getSecond());
                    WrappedSignedProperty property = new WrappedSignedProperty("textures", pair.getFirst(), pair.getSecond());
                    multimap.put("textures", property);
                }
            });
        } else {
            multimap.putAll("textures", altTextures);
        }
    }

    public JSONArray toJSONArray() {
        JSONArray jsonArray = new JSONArray();
        if (textures != null) {
            textures.forEach(new Consumer<Pair<String, String>>() {
                @Override
                public void accept(Pair<String, String> pair) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("value", pair.getFirst());
                    jsonObject.put("signature", pair.getSecond());
                    jsonArray.add(jsonObject);
                }
            });
        } else {
            altTextures.forEach(new Consumer<WrappedSignedProperty>() {
                @Override
                public void accept(WrappedSignedProperty wrappedSignedProperty) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("value", wrappedSignedProperty.getValue());
                    jsonObject.put("signature", wrappedSignedProperty.getSignature());
                    jsonArray.add(jsonObject);
                }
            });
        }
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
