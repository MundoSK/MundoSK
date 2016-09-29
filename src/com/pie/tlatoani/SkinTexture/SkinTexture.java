package com.pie.tlatoani.SkinTexture;

import ch.njol.util.Pair;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;
import com.pie.tlatoani.Mundo;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public static final SkinTexture STEVE = new SkinTexture(
            "eyJ0aW1lc3RhbXAiOjE0NzQyMTc3NjkwMDAsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE5MmI0NTY2ZjlhMjg2OTNlNGMyNGFiMTQxNzJjZDM0MjdiNzJiZGE4ZjM0ZDRhNjEwODM3YTQ3ZGEwZGUifX19",
            "pRQbSEnKkNmi0uW7r8H4xzoWS3E4tkWNbiwwRYgmvITr0xHWSKii69TcaYDoDBXGBwZ525Ex5z5lYe5Xg6zb7pyBPiTJj8J0QdKenQefVnm6Vi1SAR1uN131sRddgK2Gpb2z0ffsR9USDjJAPQtQwCqz0M7sHeXUJhuRxnbznpuZwGq+B34f1TqyVH8rcOSQW9zd+RY/MEUuIHxmSRZlfFIwYVtMCEmv4SbhjLNIooGp3z0CWqDhA7GlJcDFb64FlsJyxrAGnAsUwL2ocoikyIQceyj+TVyGIEuMIpdEifO6+NkCnV7v+zTmcutOfA7kHlj4d1e5ylwi3/3k4VKZhINyFRE8M8gnLgbVxNZ4mNtI3ZMWmtmBnl9dVujyo+5g+vceIj5Admq6TOE0hy7XoDVifLWyNwO/kSlXl34ZDq1MCVN9f1ryj4aN7BB8/Tb2M4sJf3YoGi0co0Hz/A4y14M5JriG21lngw/vi5Pg90GFz64ASssWDN9gwuf5xPLUHvADGo0Bue8KPZPyI0iuIi/3sZCQrMcdyVcur+facIObTQhMut71h8xFeU05yFkQUOKIQswaz2fpPb/cEypWoSCeQV8T0w0e3YKLi4RaWWvKS1MFJDHn7xMYaTk0OhALJoV5BxRD8vJeRi5jYf3DjEgt9+xB742HrbVRDlJuTp4="
    );
    public static final SkinTexture ALEX = new SkinTexture(
            "eyJ0aW1lc3RhbXAiOjE0NzQyMTc5MjMyMDAsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn0sInVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTcxMDFmYTQ3NWI2NjA1NmQ2ZDcxZjJmZDI1Y2NkZmI1NjNhOTI1NGZhMjEzZTYyOGNkN2Q4MWQxZWVlOGUifX19",
            "vwUYP+IUwLgb5X4EEzZ9ThB8Pv2hq2LZWuSUr8i/FWcmCc9C4Q4FzxbeMPFKZihVdL7zL2cnmuTiXwxo7TewDjH0S4pIIm2fIvuYKSgoAjStVozL81vdWnhIuB5nNlgigjFLTuWMol36upujFcSDhvzF2ebZQprOEYWVjo3BjqccMBYsz4Uqy8/Kl2dzvPK7V8A167+Zt2l1LTkSBMMmvYoBHYC+L0eu5OCAe81WdtpXHAsKbVcz1VSGKNKhXE+eh2PsC5OHNQo7hc3H3gfVksrrJXjx3TmA5XFzA/7JAz3jmtYWhe3YGoJlZIBC9Y1WVK99c+yHl2x6TJUjwIS6IGqicNcSlhuqu51qnz6ICp7nklK7UPWA0lCME5Ufxu4Ao5aU5F4C9erelJt/t40vWq/2NiBaz7YUjOFZ2gvq1CKnnJnNjqbW0fuZsU4Gc1PtGiX36teq5BBNew7vmOWK0KmObUlXFoF2/tCsbYKP+GiJ8PG+XxGJ5OImIznmh/Y/ZI3tcRdcw8SL8UvgbdqaGjeScq+az8iHxLGSEHwu6ZGdkq3I3oJxUz7eCLkfrqhbRWOwQ8YHh8oz48iGLxiQoElQqzwEIbr6qaXrvCWam0ZcyLc2T9u+K9PcAnUFF781YIveI3kuUytQVm+kbWeb0+31xAzQfrOCFOP3O1WEIMU="
    );

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
