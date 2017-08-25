package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Util.EvolvingPropertyExpression;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprNameOfCreator extends EvolvingPropertyExpression<WorldCreatorData, String> {
    @Override
    public WorldCreatorData set(WorldCreatorData worldCreatorData, String s) {
        return worldCreatorData.setName(Optional.of(s));
    }

    @Override
    public WorldCreatorData reset(WorldCreatorData worldCreatorData) {
        return worldCreatorData.setName(Optional.empty());
    }

    @Override
    public String convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.name.orElse(null);
    }
}
