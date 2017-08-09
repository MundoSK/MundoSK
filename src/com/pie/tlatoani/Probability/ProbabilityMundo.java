package com.pie.tlatoani.Probability;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Mundo;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ProbabilityMundo {
    
    public static void load() {
        Mundo.registerScope(ScopeProbability.class, "prob[ability]", "random chance");
        Mundo.registerCondition(CondProbabilityValue.class, "%number% prob[ability]");
        Mundo.registerExpression(ExprRandomIndex.class,String.class, ExpressionType.PROPERTY,"random from %numbers% prob[abilitie]s");
        Mundo.registerExpression(ExprRandomNumberIndex.class,Integer.class,ExpressionType.PROPERTY,"random number from %numbers% prob[abilitie]s");
    }
}
