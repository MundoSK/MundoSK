package com.pie.tlatoani.Skin;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.json.simple.JSONObject;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Tlatoani on 8/3/16.
 */
public class Skin {
    public static final Skin EMPTY = new Skin("", "");
    public static final Skin STEVE = new Skin(
            "eyJ0aW1lc3RhbXAiOjE0NzQyMTc3NjkwMDAsInByb2ZpbGVJZCI6ImIwZDRiMjhiYzFkNzQ4ODlhZjBlODY2MWNlZTk2YWFiIiwicHJvZmlsZU5hbWUiOiJJbnZlbnRpdmVHYW1lcyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE5MmI0NTY2ZjlhMjg2OTNlNGMyNGFiMTQxNzJjZDM0MjdiNzJiZGE4ZjM0ZDRhNjEwODM3YTQ3ZGEwZGUifX19",
            "pRQbSEnKkNmi0uW7r8H4xzoWS3E4tkWNbiwwRYgmvITr0xHWSKii69TcaYDoDBXGBwZ525Ex5z5lYe5Xg6zb7pyBPiTJj8J0QdKenQefVnm6Vi1SAR1uN131sRddgK2Gpb2z0ffsR9USDjJAPQtQwCqz0M7sHeXUJhuRxnbznpuZwGq+B34f1TqyVH8rcOSQW9zd+RY/MEUuIHxmSRZlfFIwYVtMCEmv4SbhjLNIooGp3z0CWqDhA7GlJcDFb64FlsJyxrAGnAsUwL2ocoikyIQceyj+TVyGIEuMIpdEifO6+NkCnV7v+zTmcutOfA7kHlj4d1e5ylwi3/3k4VKZhINyFRE8M8gnLgbVxNZ4mNtI3ZMWmtmBnl9dVujyo+5g+vceIj5Admq6TOE0hy7XoDVifLWyNwO/kSlXl34ZDq1MCVN9f1ryj4aN7BB8/Tb2M4sJf3YoGi0co0Hz/A4y14M5JriG21lngw/vi5Pg90GFz64ASssWDN9gwuf5xPLUHvADGo0Bue8KPZPyI0iuIi/3sZCQrMcdyVcur+facIObTQhMut71h8xFeU05yFkQUOKIQswaz2fpPb/cEypWoSCeQV8T0w0e3YKLi4RaWWvKS1MFJDHn7xMYaTk0OhALJoV5BxRD8vJeRi5jYf3DjEgt9+xB742HrbVRDlJuTp4="
    );
    public static final Skin ALEX = new Skin(
            "eyJ0aW1lc3RhbXAiOjE0NzQyMTc5MjMyMDAsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn0sInVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTcxMDFmYTQ3NWI2NjA1NmQ2ZDcxZjJmZDI1Y2NkZmI1NjNhOTI1NGZhMjEzZTYyOGNkN2Q4MWQxZWVlOGUifX19",
            "vwUYP+IUwLgb5X4EEzZ9ThB8Pv2hq2LZWuSUr8i/FWcmCc9C4Q4FzxbeMPFKZihVdL7zL2cnmuTiXwxo7TewDjH0S4pIIm2fIvuYKSgoAjStVozL81vdWnhIuB5nNlgigjFLTuWMol36upujFcSDhvzF2ebZQprOEYWVjo3BjqccMBYsz4Uqy8/Kl2dzvPK7V8A167+Zt2l1LTkSBMMmvYoBHYC+L0eu5OCAe81WdtpXHAsKbVcz1VSGKNKhXE+eh2PsC5OHNQo7hc3H3gfVksrrJXjx3TmA5XFzA/7JAz3jmtYWhe3YGoJlZIBC9Y1WVK99c+yHl2x6TJUjwIS6IGqicNcSlhuqu51qnz6ICp7nklK7UPWA0lCME5Ufxu4Ao5aU5F4C9erelJt/t40vWq/2NiBaz7YUjOFZ2gvq1CKnnJnNjqbW0fuZsU4Gc1PtGiX36teq5BBNew7vmOWK0KmObUlXFoF2/tCsbYKP+GiJ8PG+XxGJ5OImIznmh/Y/ZI3tcRdcw8SL8UvgbdqaGjeScq+az8iHxLGSEHwu6ZGdkq3I3oJxUz7eCLkfrqhbRWOwQ8YHh8oz48iGLxiQoElQqzwEIbr6qaXrvCWam0ZcyLc2T9u+K9PcAnUFF781YIveI3kuUytQVm+kbWeb0+31xAzQfrOCFOP3O1WEIMU="
    );
    public static final Skin ALL_WHITE = new Skin(
            "eyJ0aW1lc3RhbXAiOjE0NzAwMjgwNDU3MzUsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iNTg3OTM1YzdmYmVjYzJmYWMxMDY0OWZjZGZiODM1YjQ2NTA3MzZiOWJmMWQ0NGVhZjc2ZDNiOWVmN2UwIn19fQ==",
            "eTy8+/waBl22GpAyTHx+QY40J3DY57F2FSkVupjJxAuuUfstvX/DxmJANKtIcYCYP9LUHh9DkP1T2bXUobHcx8GAICi8S/uEWXx96PHHjSr7wQ9uBC4NMCkV7dHHMKdVqEJ9jDpMvSax9vs1tOc2NWaeMbzc/345K95JaYVD+AV4W1+IuppXlMgDmCatUCgGDbzTuQKO8An9zFPciCRq1VSGaOPCj4PoIDQyMhSPqb1cPML/wH26Wtl4DEjnyVIyemk7oDBK29DXxtBLmzX6Ni1C8VM3UmG2StDC7dSwxJNLBHQ/aqXwupK4j0bZghiRbiaq4kAlPcpMeL+TTHac7oYFGihj/s/OVWaL0Fo2KgFZgKuZ26kDepCLEEOOoj2Zq8ohtxufPdTDqw032AyA/HbldnBIsCnQCDiq3XXdZHz0R+pvuf73BSHc7CiG2pwjSdSQ8XetlP70A9SddJu+iFuKGwzh/cvQ2H+sqoUYmIYIXcl2xJTy+Y/shxJDZZVxGCSHmj+4SYzJCg+nsNlEJ9HBG//LfeY+WhacbC9pPPy8wKnDqvIx0QX2YakyBFy659DEBEhSSNRQjOm78Zd9K7pP1QOrS2RDwsDSIXaR0gxT69Bv+Z/r+w8GJY6tHvT8aqTNQHpmv+kwMVdGOWMj3wMErW2aqjH9ffc1nuWht/E="
    );

    public static String MULTIMAP_KEY = "textures";
    public final String value;
    public final String signature;
    public final UUID uuid; //Currently used to ensure uniqueness when creating a skull using a skin

    public Skin(String value, String signature) {
        this(value, signature, UUID.randomUUID());
    }

    public Skin(String value, String signature, UUID uuid) {
        this.value = Optional.ofNullable(value).orElse("");
        this.signature = Optional.ofNullable(signature).orElse("");
        this.uuid = uuid;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", value);
        jsonObject.put("signature", signature);
        return jsonObject;
    }

    public WrappedSignedProperty toWrappedSignedProperty() {
        return new WrappedSignedProperty("textures", value, signature);
    }

    public WrappedGameProfile toGameProfile(String name) {
        WrappedGameProfile wrappedGameProfile = new WrappedGameProfile(uuid, name);
        wrappedGameProfile.getProperties().put(MULTIMAP_KEY, toWrappedSignedProperty());
        return wrappedGameProfile;
    }

    @Override
    public String toString() {
        return toJSON().toJSONString();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Skin) {
            Skin otherSkin = (Skin) other;
            return value.equals(otherSkin.value) && signature.equals(otherSkin.signature);
        } else {
            return false;
        }
    }

    //44701 is prime
    @Override
    public int hashCode() {
        return 13 + value.hashCode() + (44701 * signature.hashCode());
    }

    //Creation Methods

    public static Skin fromJSON(JSONObject jsonObject) {
        return new Skin((String) jsonObject.get("value"), (String) jsonObject.get("signature"));
    }

    public static Skin fromJSON(JSONObject jsonObject, UUID uuid) {
        return new Skin((String) jsonObject.get("value"), (String) jsonObject.get("signature"), uuid);
    }

    public static Skin fromWrappedSignedProperty(WrappedSignedProperty wrappedSignedProperty, UUID uuid) {
        return new Skin(wrappedSignedProperty.getValue(), wrappedSignedProperty.getSignature(), uuid);
    }

    public static Skin fromGameProfile(WrappedGameProfile gameProfile) {
        WrappedSignedProperty[] wrappedSignedProperties = gameProfile.getProperties().get(MULTIMAP_KEY).toArray(new WrappedSignedProperty[0]);
        if (wrappedSignedProperties.length == 0) {
            return Skin.EMPTY;
        } else {
            return fromWrappedSignedProperty(wrappedSignedProperties[0], gameProfile.getUUID());
        }
    }
}
