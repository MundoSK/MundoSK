package com.pie.tlatoani.Skin.Retrieval;

import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Core.Static.Logging;
import mundosk_libraries.light_jsoup.Connection;
import mundosk_libraries.light_jsoup.HttpConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Tlatoani on 5/7/17.
 * Based on (and with portions of code copied from) the Mineskin Client created by inventivetalent (who also created the Mineskin API)
 */
public class MineSkinClient {
    public static final String DEFAULT_SKIN_OPTIONS = "";
    public static final String ALEX_SKIN_OPTIONS = "model=slim";
    public static final String MINESKIN_URL_FORMAT = "https://api.mineskin.org/generate/url?url=%s&%s";
    public static final String MINESKIN_UPLOAD_FORMAT = "https://api.mineskin.org/generate/upload?%s";
    public static final String USER_AGENT = "MundoSK-MineSkin-JavaClient";

    public static String mineSkinFromUrl(String url, int timeoutMillis, boolean def) {
        try {
            Connection connection = HttpConnection
                    .connect(String.format(MINESKIN_URL_FORMAT, url, def ? DEFAULT_SKIN_OPTIONS : ALEX_SKIN_OPTIONS))
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.POST)
                    .ignoreContentType(true)
                    .timeout(timeoutMillis);
            return connection.execute().body();
        } catch (Exception e) {
            Logging.debug(MineSkinClient.class, e);
            return null;
        }
    }

    public static String mineSkinFromFile(File file, int timeoutMillis, boolean def) {
        try {
            Connection connection = HttpConnection
                    .connect(String.format(MINESKIN_UPLOAD_FORMAT, def ? DEFAULT_SKIN_OPTIONS : ALEX_SKIN_OPTIONS))
                    .userAgent(USER_AGENT)
                    .method(Connection.Method.POST)
                    .data("file", file.getName(), new FileInputStream(file))
                    .ignoreContentType(true)
                    .timeout(timeoutMillis);
            return connection.execute().body();
        } catch (Exception e) {
            Logging.debug(MineSkinClient.class, e);
            return null;
        }
    }

    public static Skin fromMineSkinString(String string) {
        Logging.debug(MineSkinClient.class, "fromMineSkinString(string = " + string + ")");
        if (string == null) {
            return null;
        }
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(string);
            if (jsonObject.containsKey("error")) {
                Logging.debug(MineSkinClient.class, "fromMineSkinString() error: " + jsonObject.get("error") + ", code: " + jsonObject.get("err"));
                return null;
            } else {
                JSONObject subJSON = (JSONObject) (
                        (JSONObject) jsonObject.get("data")
                ).get("texture");
                return Skin.fromJSON(subJSON);
            }
        } catch (NullPointerException | ParseException | ClassCastException e) {
            Logging.debug(MineSkinClient.class, e);
            return null;
        }
    }

}
