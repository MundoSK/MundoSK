package com.pie.tlatoani.ZExperimental;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SyntaxElementInfo;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tlatoani on 2/25/17.
 */
public class EffCustom extends Effect {
    private static final ModifiableSyntaxElementInfo<EffCustom> info = new ModifiableSyntaxElementInfo<>(new String[0], EffCustom.class);
    private static final ArrayList<CustomEffect> customEffects = new ArrayList<>();

    private CustomEffect customEffect;
    private HashMap<String, Expression<?>> organizedExprs;
    private SkriptParser.ParseResult parseResult;

    public static void onLoad() {
        UtilSyntaxRegistration.registerEffect(info);
    }

    public static void registerEffect(CustomEffect customEffect) {
        customEffects.add(customEffect);
        String[] newPatterns = new String[info.patterns.length + 1];
        newPatterns[info.patterns.length] = customEffect.getSyntax();
        System.arraycopy(info.patterns, 0, newPatterns, 0, info.patterns.length);
        info.setPatterns(newPatterns);
    }

    public static void unregisterEffect(CustomEffect customEffect) {
        int index = customEffects.indexOf(customEffect);
        if (index < 0) {
            throw new IllegalArgumentException(customEffect + " was not registered as a custom effect!");
        }
        customEffects.remove(index);
        String[] newPatterns = new String[info.patterns.length - 1];
        System.arraycopy(info.patterns, 0, newPatterns, 0, index);
        System.arraycopy(info.patterns, index + 1, newPatterns, index, newPatterns.length - index);
        info.setPatterns(newPatterns);
    }

    public static void unregisterAllEffects() {
        customEffects.clear();
        info.setPatterns();
    }

    @Override
    protected void execute(Event event) {
        customEffect.execute(event, organizedExprs, parseResult);
    }

    @Override
    public String toString(Event event, boolean b) {
        return customEffect.getSyntax();
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        customEffect = customEffects.get(i);
        organizedExprs = customEffect.organizeExprs(expressions);
        this.parseResult = parseResult;
        return true;
    }
}
