package com.pie.tlatoani.ZExperimental;

import com.pie.tlatoani.Core.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ZExperimentalMundo {

    public static void load() {
        Registration.registerEvent("Custom Element Event", CustomEffect.class, CustomElementEvent.class, "[light_jsoup] [new] [custom] effect %string%");
    }
}
