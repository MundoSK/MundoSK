package com.pie.tlatoani.Skin;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Multimap;
import com.pie.tlatoani.Util.UtilReflection;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 8/3/16.
 */
public abstract class Skin {
    public static final Skin STEVE = new Simple(
            "eyJ0aW1lc3RhbXAiOjE0NzQyMTc3NjkwMDAsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE5MmI0NTY2ZjlhMjg2OTNlNGMyNGFiMTQxNzJjZDM0MjdiNzJiZGE4ZjM0ZDRhNjEwODM3YTQ3ZGEwZGUifX19",
            "pRQbSEnKkNmi0uW7r8H4xzoWS3E4tkWNbiwwRYgmvITr0xHWSKii69TcaYDoDBXGBwZ525Ex5z5lYe5Xg6zb7pyBPiTJj8J0QdKenQefVnm6Vi1SAR1uN131sRddgK2Gpb2z0ffsR9USDjJAPQtQwCqz0M7sHeXUJhuRxnbznpuZwGq+B34f1TqyVH8rcOSQW9zd+RY/MEUuIHxmSRZlfFIwYVtMCEmv4SbhjLNIooGp3z0CWqDhA7GlJcDFb64FlsJyxrAGnAsUwL2ocoikyIQceyj+TVyGIEuMIpdEifO6+NkCnV7v+zTmcutOfA7kHlj4d1e5ylwi3/3k4VKZhINyFRE8M8gnLgbVxNZ4mNtI3ZMWmtmBnl9dVujyo+5g+vceIj5Admq6TOE0hy7XoDVifLWyNwO/kSlXl34ZDq1MCVN9f1ryj4aN7BB8/Tb2M4sJf3YoGi0co0Hz/A4y14M5JriG21lngw/vi5Pg90GFz64ASssWDN9gwuf5xPLUHvADGo0Bue8KPZPyI0iuIi/3sZCQrMcdyVcur+facIObTQhMut71h8xFeU05yFkQUOKIQswaz2fpPb/cEypWoSCeQV8T0w0e3YKLi4RaWWvKS1MFJDHn7xMYaTk0OhALJoV5BxRD8vJeRi5jYf3DjEgt9+xB742HrbVRDlJuTp4="
    );
    public static final Skin ALEX = new Simple(
            "eyJ0aW1lc3RhbXAiOjE0NzQyMTc5MjMyMDAsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn0sInVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTcxMDFmYTQ3NWI2NjA1NmQ2ZDcxZjJmZDI1Y2NkZmI1NjNhOTI1NGZhMjEzZTYyOGNkN2Q4MWQxZWVlOGUifX19",
            "vwUYP+IUwLgb5X4EEzZ9ThB8Pv2hq2LZWuSUr8i/FWcmCc9C4Q4FzxbeMPFKZihVdL7zL2cnmuTiXwxo7TewDjH0S4pIIm2fIvuYKSgoAjStVozL81vdWnhIuB5nNlgigjFLTuWMol36upujFcSDhvzF2ebZQprOEYWVjo3BjqccMBYsz4Uqy8/Kl2dzvPK7V8A167+Zt2l1LTkSBMMmvYoBHYC+L0eu5OCAe81WdtpXHAsKbVcz1VSGKNKhXE+eh2PsC5OHNQo7hc3H3gfVksrrJXjx3TmA5XFzA/7JAz3jmtYWhe3YGoJlZIBC9Y1WVK99c+yHl2x6TJUjwIS6IGqicNcSlhuqu51qnz6ICp7nklK7UPWA0lCME5Ufxu4Ao5aU5F4C9erelJt/t40vWq/2NiBaz7YUjOFZ2gvq1CKnnJnNjqbW0fuZsU4Gc1PtGiX36teq5BBNew7vmOWK0KmObUlXFoF2/tCsbYKP+GiJ8PG+XxGJ5OImIznmh/Y/ZI3tcRdcw8SL8UvgbdqaGjeScq+az8iHxLGSEHwu6ZGdkq3I3oJxUz7eCLkfrqhbRWOwQ8YHh8oz48iGLxiQoElQqzwEIbr6qaXrvCWam0ZcyLc2T9u+K9PcAnUFF781YIveI3kuUytQVm+kbWeb0+31xAzQfrOCFOP3O1WEIMU="
    );
    public static final Skin EMPTY = new Simple("", "");

    private static UtilReflection.FieldAccessor gameProfile = null;

    static {
        gameProfile = UtilReflection.getField(UtilReflection.getCraftBukkitClass("inventory.CraftMetaSkull"), "profile", UtilReflection.getClass("com.mojang.authlib.GameProfile"));
    }

    public static Skin getSkinOfSkull(SkullMeta skullMeta) {
        WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromHandle(gameProfile.get(skullMeta));
        return wrappedGameProfile == null ? EMPTY : new Skin.Collected(wrappedGameProfile.getProperties().get("textures"));
    }

    public static void setSkinOfSKull(SkullMeta skullMeta, Skin skin) {
        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(UUID.fromString("10001000-1000-3000-8000-100010001000"), "MundoSK-Name");
        skin.retrieveSkinTextures(wrappedGameProfile.getProperties());
        gameProfile.set(skullMeta, wrappedGameProfile.getHandle());
    }

    public abstract void retrieveSkinTextures(Multimap<String, WrappedSignedProperty> multimap);

    public abstract JSONArray toJSONArray();

    @Override
    public String toString() {
        return toJSONArray().toJSONString();
    }

    @Override
    public final boolean equals(Object other) {
        return other instanceof Skin && toString().equals(other.toString());
    }

    public static class Simple extends Skin {
        private String value;
        private String signature;

        public Simple(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }

        public void retrieveSkinTextures(Multimap<String, WrappedSignedProperty> multimap) {
            multimap.removeAll("textures");
            multimap.put("textures", new WrappedSignedProperty("textures", value, signature));
        }

        public JSONArray toJSONArray() {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", value);
            jsonObject.put("signature", signature);
            jsonArray.add(jsonObject);
            return jsonArray;
        }
    }

    public static class JSON extends Skin {
        private JSONArray textures = new JSONArray();


        public JSON(JSONArray... jsonArrays) {
            for (JSONArray jsonArray : jsonArrays) {
                for (Object o : jsonArray) {
                    textures.add(new JSONObject((JSONObject) o));
                }
            }
        }

        public void retrieveSkinTextures(Multimap<String, WrappedSignedProperty> multimap) {
            multimap.removeAll("textures");
            for (Object o : textures) {
                JSONObject jsonObject = (JSONObject) o;
                WrappedSignedProperty property = new WrappedSignedProperty("textures", (String) jsonObject.get("value"), (String) jsonObject.get("signature"));
                multimap.put("textures", property);
            }
        }

        public JSONArray toJSONArray() {
            JSONArray jsonArray = new JSONArray();
            for (Object o : textures) {
                jsonArray.add(new JSONObject((JSONObject) o));
            }
            return jsonArray;
        }

        @Override
        public String toString() {
            return textures.toJSONString();
        }
    }

    public static class Collected extends Skin {
        private Collection<WrappedSignedProperty> textures;

        public Collected(Collection<WrappedSignedProperty> collection) {
            textures = new ArrayList<>(collection);
        }

        public void retrieveSkinTextures(Multimap<String, WrappedSignedProperty> multimap) {
            multimap.removeAll("textures");
            multimap.putAll("textures", textures);
        }

        public JSONArray toJSONArray() {
            JSONArray jsonArray = new JSONArray();
            for (WrappedSignedProperty wrappedSignedProperty : textures) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("value", wrappedSignedProperty.getValue());
                jsonObject.put("signature", wrappedSignedProperty.getSignature());
                jsonArray.add(jsonObject);
            }
            return jsonArray;
        }
    }
}
