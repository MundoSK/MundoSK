package com.pie.tlatoani.Util.Skript;

import ch.njol.skript.config.Config;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.Trigger;

/**
 * Created by Tlatoani on 8/11/17.
 */
public abstract class MundoEventScope extends SelfRegisteringSkriptEvent {
    private boolean afterInitRun = false;

    @Override
    public void register(Trigger trigger) {
        if (!afterInitRun) {
            afterInit();
            afterInitRun = true;
        }
    }

    public void afterParse(Config config) {
        if (!afterInitRun) {
            afterInit();
            afterInitRun = true;
        }
    }

    protected abstract void afterInit();

}
