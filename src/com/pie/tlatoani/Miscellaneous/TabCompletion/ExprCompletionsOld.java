package com.pie.tlatoani.Miscellaneous.TabCompletion;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pie.tlatoani.Util.Skript.ListExpression;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Tlatoani on 6/29/17.
 */
public class ExprCompletionsOld extends ListExpression<String> {
    @Override
    protected String[] get(Event event) {
        if (event instanceof PlayerChatTabCompleteEvent) {
            return ((PlayerChatTabCompleteEvent) event).getTabCompletions().toArray(new String[0]);
        }
        return new String[0];
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "completions";
    }

    @Override
    public boolean subInit(Expression<?>[] expression, int matchedPattern, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(PlayerChatTabCompleteEvent.class)) {
            Skript.error("The 'completions' expression can only be used in the 'on chat tab complete' event!");
            return false;
        }
        return true;
    }

    @Override
    public boolean isSettable() {
        return true;
    }

    @Override
    public void set(Event event, String[] value) {
        if (event instanceof PlayerChatTabCompleteEvent) {
            Collection<String> completions = ((PlayerChatTabCompleteEvent) event).getTabCompletions();
            ArrayList<String> newcompletions = new ArrayList<String>(Arrays.asList(value));
            String[] completionsarray = completions.toArray(new String[0]);
            for (int i = 0; i < completionsarray.length; i++) {
                if (newcompletions.contains(completionsarray[i])) {
                    newcompletions.remove(completionsarray[i]);
                } else {
                    completions.remove(completionsarray[i]);
                }
            }
            for (int i = 0; i < newcompletions.size(); i++) {
                completions.add(newcompletions.get(i));
            }
        }
    }

    @Override
    public String getResettedValue() {
        return "";
    }
}
