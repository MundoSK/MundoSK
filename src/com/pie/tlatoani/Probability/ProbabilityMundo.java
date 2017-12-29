package com.pie.tlatoani.Probability;

import ch.njol.skript.lang.ExpressionType;
import com.pie.tlatoani.Registration.Registration;

/**
 * Created by Tlatoani on 8/8/17.
 */
public class ProbabilityMundo {
    
    public static void load() {
        Registration.registerScope(ScopeProbability.class, "prob[ability]", "random chance")
                .document("Probability", "1.5.3 or earlier", "The probability scope is used to perform different actions based on given probabilities. "
                        + "Under the scope, you write different probability values using the syntax '%number% prob[ability]'. "
                        + "These can be written as scopes or just simple lines of code. When the probability scope is run, all of these "
                        + "probabilities are added up and then, using their values, one is chosen at random and the code starts to proceed "
                        + "from there. First, if the chosen probability is a scope, its section of code is run. Then, the rest of the lines of code "
                        + "after the chosen probability are run. If another probability is encountered, it is ignored and the section of code under it "
                        + "(if it is a scope) is also ignored, but the lines of code that come after it are still run.");
        Registration.registerCondition(CondProbabilityValue.class, "%number% prob[ability]");
        Registration.registerExpression(ExprRandomIndex.class,String.class, ExpressionType.PROPERTY,"random from %numbers% prob[abilitie]s")
                .document("Random Index from List Variable", "1.5.3 or earlier", "An expression that takes a list variable containing numbers, "
                        + "then adds all of the numbers up, assigns each index a probability based on its number value divided by the total sum, "
                        + "and finally chooses one index at random using the probabilities and returns it.");
        Registration.registerExpression(ExprRandomNumberIndex.class,Integer.class,ExpressionType.PROPERTY,"random number from %numbers% prob[abilitie]s")
                .document("Random Index from Numbers", "1.5.3 or earlier", "An expression that takes a list of numbers, "
                        + "then adds all of the numbers up, assigns each index (here, index means the position of a number in the list, ex. the first number has an index of 1) "
                        + "a probability based on its number value divided by the total sum, "
                        + "and finally chooses one index at random using the probabilities and returns it.");
    }
}
