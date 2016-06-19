package com.pie.tlatoani.Miscellaneous;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import com.pie.tlatoani.ListUtil.Transformer;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Tlatoani on 6/16/16.
 */
public class TransCompletions implements Transformer<String> {
    @Override
    public Boolean init(Expression expression) {
        if (expression != null) {
            Skript.error("The 'completions' expression is used on its own!");
            return false;
        }
        if (!ScriptLoader.isCurrentEvent(PlayerChatTabCompleteEvent.class)) {
            Skript.error("The 'completions' expression can only be used in the 'on chat tab complete' event!");
            return false;
        }
        return true;
    }

    @Override
    public Class getType() {
        return null;
    }

    @Override
    public Boolean isSettable() {
        return null;
    }

    @Override
    public String[] get(Event event) {
        if (event instanceof PlayerChatTabCompleteEvent) {
            return ((PlayerChatTabCompleteEvent) event).getTabCompletions().toArray(new String[0]);
        }
        return new String[0];
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
}
