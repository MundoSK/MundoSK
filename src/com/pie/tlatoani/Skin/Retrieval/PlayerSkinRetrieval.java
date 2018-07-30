package com.pie.tlatoani.Skin.Retrieval;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.pie.tlatoani.Core.Static.Config;
import com.pie.tlatoani.Core.Static.Logging;
import com.pie.tlatoani.Core.Static.Scheduling;
import com.pie.tlatoani.Skin.ProfileManager;
import com.pie.tlatoani.Skin.Skin;
import mundosk_libraries.light_jsoup.Connection;
import mundosk_libraries.light_jsoup.HttpConnection;
import org.bukkit.OfflinePlayer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerSkinRetrieval {
    public static final String MOJANG_FROM_UUID = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    private static Cache<UUID, Skin> skinCache = null;

    private static synchronized Cache<UUID, Skin> skinCache(boolean set, Cache<UUID, Skin> newCache) {
        if (set) {
            skinCache = newCache;
        }
        return skinCache;
    }

    //should only be run in sync
    public static void reloadSkinCache() {
        if (Config.IMPLEMENT_PACKET_STUFF.getCurrentValue() && Config.ENABLE_OFFLINE_PLAYER_SKIN_CACHE.getCurrentValue()) {
            CacheBuilder builder = CacheBuilder.newBuilder();
            builder.maximumSize(Config.OFFLINE_PLAYER_SKIN_CACHE_MAX_SIZE.getCurrentValue());
            builder.expireAfterAccess(Config.OFFLINE_PLAYER_SKIN_CACHE_EXPIRE_TIME_MINUTES.getCurrentValue(), TimeUnit.MINUTES);
            skinCache(true, builder.build());
        } else {
            skinCache(true, null);
        }
    }

    //should only be run in async
    public static Skin retrieveSkin(OfflinePlayer offlinePlayer, int timeoutMillis) {
        try {
            Cache<UUID, Skin> skinCache = skinCache(false, null);
            UUID uuid = offlinePlayer.getUniqueId();
            Skin skin = skinCache == null ? null : skinCache.getIfPresent(uuid);
            if (skin == null) {
                Skin[] array = new Skin[0];
                Scheduling.syncLock(() -> {
                    if (offlinePlayer.isOnline()) {
                        array[0] = ProfileManager.getProfile(offlinePlayer.getPlayer()).getActualSkin();
                    }
                });
                skin = array[0];
                if (skin == null) {
                    skin = fromMojangString(uuid, mojangFromUUID(uuid, timeoutMillis));
                    if (skin != null && skinCache != null) { //if the skin is of an online player, don't put it in the cache
                        skinCache.put(uuid, skin);
                    }
                }
            }
            return skin;
        } catch (ParseException | IOException e) {
            Logging.debug(PlayerSkinRetrieval.class, e);
            return null;
        }
    }

    //should only be run in async
    public static Skin retrieveOfflineSkin(OfflinePlayer offlinePlayer, int timeoutMillis) {
        try {
            Cache<UUID, Skin> skinCache = skinCache(false,null);
            UUID uuid = offlinePlayer.getUniqueId();
            Skin skin = skinCache == null ? null : skinCache.getIfPresent(uuid);
            if (skin == null) {
                skin = fromMojangString(uuid, mojangFromUUID(uuid, timeoutMillis));
                if (skin != null && skinCache != null) {
                    skinCache.put(uuid, skin);
                }
            }
            return skin;
        } catch (ParseException | IOException e) {
            Logging.debug(PlayerSkinRetrieval.class, e);
            return null;
        }
    }

    private static Skin fromMojangString(UUID uuid, String string) throws ParseException {
        Logging.debug(MineSkinClient.class, "fromMojangString(uuid = " + uuid + ", string = " + string + ")");
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(string);
        if (jsonObject.containsKey("error")) {
            Logging.debug(MineSkinClient.class, "fromMojangString() error: " + jsonObject.get("error") + ", message: " + jsonObject.get("errorMessage"));
            return null;
        } else {
            JSONObject subJSON = (JSONObject) jsonObject.get("properties");
            return Skin.fromJSON(subJSON, uuid);
        }
    }

    private static String mojangFromUUID(UUID uuid, int timeoutMillis) throws IOException {
        Connection connection = HttpConnection
                .connect(String.format(MOJANG_FROM_UUID, uuid.toString()))
                .method(Connection.Method.GET)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .timeout(timeoutMillis);
        return connection.execute().body();
    }
}
