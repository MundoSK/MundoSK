package com.pie.tlatoani;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Achievement.*;
import org.bukkit.Achievement;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

/**
 * Created by Tlatoani on 5/30/17.
 *
 * Used to register effects/expressions/etc. specific to versions prior to Minecraft 1.12
 */
public class RegistryPreColorUpdate {

    private RegistryPreColorUpdate() {} //Cannot be initialized

    public static void register() {
        //Achievements
        Mundo.registerEnum(Achievement.class, "achievement", Achievement.values());
        Mundo.registerEffect(EffAwardAch.class, "award achieve[ment] %achievement% to %player%");
        Mundo.registerEffect(EffRemoveAch.class, "remove achieve[ment] %achievement% from %player%");
        Mundo.registerEvent("Achievement Award", EvtAchAward.class, PlayerAchievementAwardedEvent.class, "achieve[ment] [%-achievement%] award", "award of achieve[ment] [%-achievement%]");
        Mundo.registerEventValue(PlayerAchievementAwardedEvent.class, Achievement.class, PlayerAchievementAwardedEvent::getAchievement);
        Mundo.registerExpression(ExprParentAch.class,Achievement.class, ExpressionType.PROPERTY,"parent of achieve[ment] %achievement%");
        Mundo.registerExpression(ExprAllAch.class,Achievement.class,ExpressionType.PROPERTY,"achieve[ment]s of %player%", "%player%'s achieve[ment]s");
        Mundo.registerExpression(ExprHasAch.class,Boolean.class,ExpressionType.PROPERTY,"%player% has achieve[ment] %achievement%");
    }
}
