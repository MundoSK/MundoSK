package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.util.Timespan;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import org.bukkit.entity.LivingEntity;

/**
 * Created by Tlatoani on 9/3/17.
 */
public class ExprRemainingAir extends ChangeablePropertyExpression<LivingEntity, Timespan> {

    private int getAirTicks(LivingEntity livingEntity) {
        switch (getPropertyName()) {
            case "breath": return livingEntity.getRemainingAir();
            case "max breath": return livingEntity.getMaximumAir();
        }
        throw new IllegalStateException("Illegal getPropertyName() value: " + getPropertyName());
    }

    @Override
    public void change(LivingEntity livingEntity, Timespan timespan, Changer.ChangeMode changeMode) {
        int ticks;
        switch (changeMode) {
            case SET: ticks = (int) timespan.getTicks_i(); break;
            case ADD: ticks = getAirTicks(livingEntity) + (int) timespan.getTicks_i(); break;
            case REMOVE: ticks = getAirTicks(livingEntity) + (int) timespan.getTicks_i(); break;
            default: throw new IllegalArgumentException("Illegal changeMode: " + changeMode);
        }
        switch (getPropertyName()) {
            case "breath": livingEntity.setRemainingAir(ticks); break;
            case "max breath": livingEntity.setMaximumAir(ticks); break;
            default: throw new IllegalStateException("Illegal getPropertyName() value: " + getPropertyName());
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE};
    }

    @Override
    public Timespan convert(LivingEntity livingEntity) {
        return new Timespan(getAirTicks(livingEntity) * 50);
    }
}
