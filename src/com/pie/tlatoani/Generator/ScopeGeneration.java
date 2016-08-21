package com.pie.tlatoani.Generator;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Trigger;
import com.pie.tlatoani.Util.CustomScope;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 8/21/16.
 */
public class ScopeGeneration extends CustomScope {
    @Override
    public String getString() {
        return "generation";
    }

    @Override
    public boolean init() {
        if (!ScriptLoader.isCurrentEvent(SkriptChunkGenerationEvent.class)) {
            Skript.error("The 'generation' scope can only be used in a custom world generator!");
            return false;
        }
        return true;
    }

    @Override
    public void setScope() {
        if (!(scopeParent instanceof Trigger)) {
            Skript.error("The 'generation' scope should be immediately under the event scope, not below any further scopes!");
        }
        else if (scope.getNext() != null) {
            Skript.error("The 'generation' scope should be the last item in a custom generator!");
        }
    }

    @Override
    public boolean go(Event event) {
        ((SkriptChunkGenerationEvent) event).generation = first;
        return false;
    }
}
