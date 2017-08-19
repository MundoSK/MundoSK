package com.pie.tlatoani.WorldCreator;

import com.pie.tlatoani.Util.EvolvingPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprNameOfCreator extends EvolvingPropertyExpression<WorldCreatorData, String> {
    @Override
    public WorldCreatorData set(WorldCreatorData worldCreatorData, String s) {
        return worldCreatorData.setName(s);
    }

    @Override
    public String convert(WorldCreatorData worldCreatorData) {
        return worldCreatorData.name;
    }
}
