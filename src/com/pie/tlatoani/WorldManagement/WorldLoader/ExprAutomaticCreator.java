package com.pie.tlatoani.WorldManagement.WorldLoader;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import com.pie.tlatoani.WorldCreator.WorldCreatorData;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprAutomaticCreator extends ChangeablePropertyExpression<String, WorldCreatorData> {
    @Override
    public void change(String s, WorldCreatorData worldCreatorData, Changer.ChangeMode changeMode) {
        if (changeMode == Changer.ChangeMode.SET) {
            WorldLoader.setCreator(worldCreatorData.setName(s));
        } else if (changeMode == Changer.ChangeMode.DELETE) {
            WorldLoader.removeCreator(s);
        }
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET, Changer.ChangeMode.DELETE};
    }

    @Override
    public WorldCreatorData convert(String s) {
        return WorldLoader.getCreator(s);
    }
}
