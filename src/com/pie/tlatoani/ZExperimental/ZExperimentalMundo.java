package com.pie.tlatoani.ZExperimental;

import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ZExperimentalMundo {

    public static void load() {
        Mundo.registerEvent("Custom Element Event", CustomEffect.class, CustomElementEvent.class, "[light_jsoup] [new] [custom] effect %string%");
    }
}
