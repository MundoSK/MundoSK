package com.pie.tlatoani.Achievement;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Util.Registration;
import org.bukkit.Achievement;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class AchievementMundo {

    public static void load() {
        Registration.registerEnum(Achievement.class, "achievement", Achievement.values());
        Registration.registerEffect(EffAwardAch.class, "award achieve[ment] %achievement% to %player%");
        Registration.registerEffect(EffRemoveAch.class, "remove achieve[ment] %achievement% from %player%");
        Registration.registerEvent("Achievement Award", EvtAchAward.class, PlayerAchievementAwardedEvent.class, "achieve[ment] [%-achievement%] award", "award of achieve[ment] [%-achievement%]");
        Registration.registerEventValue(PlayerAchievementAwardedEvent.class, Achievement.class, PlayerAchievementAwardedEvent::getAchievement);
        Registration.registerExpression(ExprParentAch.class,Achievement.class, ExpressionType.PROPERTY,"parent of achieve[ment] %achievement%");
        Registration.registerExpression(ExprAllAch.class,Achievement.class,ExpressionType.PROPERTY,"achieve[ment]s of %player%", "%player%'s achieve[ment]s");
        Registration.registerExpression(ExprHasAch.class,Boolean.class,ExpressionType.PROPERTY,"%player% has achieve[ment] %achievement%");
    }

}
