package com.pie.tlatoani.Probability;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ProbabilityMundo {
    
    public static void load() {
        Registration.registerScope(ScopeProbability.class, "prob[ability]", "random chance");
        Registration.registerCondition(CondProbabilityValue.class, "%number% prob[ability]");
        Registration.registerExpression(ExprRandomIndex.class,String.class, ExpressionType.PROPERTY,"random from %numbers% prob[abilitie]s");
        Registration.registerExpression(ExprRandomNumberIndex.class,Integer.class,ExpressionType.PROPERTY,"random number from %numbers% prob[abilitie]s");
    }
}
