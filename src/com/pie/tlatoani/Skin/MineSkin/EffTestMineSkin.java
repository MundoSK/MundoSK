package com.pie.tlatoani.Skin.MineSkin;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Mundo;
import mundosk_libraries.mineskin.MineskinClient;
import mundosk_libraries.mineskin.data.SkinCallback;
import mundosk_libraries.mineskin.data.Texture;
import org.bukkit.event.Event;

import java.util.UUID;

/**
 * Created by Tlatoani on 7/1/17.
 */
public class EffTestMineSkin extends Effect {
    Expression<String> uuidExpr;

    @Override
    protected void execute(Event event) {
        new MineskinClient().generateUser(UUID.fromString(uuidExpr.getSingle(event)), new SkinCallback() {
            @Override
            public void done(mundosk_libraries.mineskin.data.Skin skin) {
                Texture texture = skin.data.texture;
                Mundo.debug(EffTestMineSkin.class, "Texture Value: " + texture.value);
                Mundo.debug(EffTestMineSkin.class, "Texture Signature: " + texture.signature);
            }

            @Override
            public void waiting(long delay) {
                Mundo.debug(EffTestMineSkin.class, "Waiting");
            }

            public void uploading() {
                Mundo.debug(EffTestMineSkin.class, "Uploading");
            }

            public void error(String errorMessage) {
                Mundo.debug(EffTestMineSkin.class, "Error: " + errorMessage);
            }

            public void exception(Exception exception) {
                Mundo.debug(EffTestMineSkin.class, "Exception: ");
                exception.printStackTrace();
            }

            public void parseException(Exception exception, String body) {
                Mundo.debug(EffTestMineSkin.class, "ParseException: " + body);
                exception.printStackTrace();
            }
        });
    }

    @Override
    public String toString(Event event, boolean b) {
        return "TEST MINESKIN: " + uuidExpr;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        uuidExpr = (Expression<String>) expressions[0];
        return true;
    }
}
