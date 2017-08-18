package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Util.EvolvingPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprGeneratorSettingsOfCreator extends EvolvingPropertyExpression<WorldCreatorData, String> {
    @Override
    public WorldCreatorData evolve(WorldCreatorData worldCreatorData, String s) {
        return worldCreatorData.setGeneratorSettings(s);
    }

    @Override
    public String convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.generatorSettings;
    }
}
