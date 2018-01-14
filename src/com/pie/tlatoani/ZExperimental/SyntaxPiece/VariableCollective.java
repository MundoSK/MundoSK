package com.pie.tlatoani.ZExperimental.SyntaxPiece;

import com.google.common.collect.ImmutableSet;

import java.util.*;
import java.util.function.Consumer;

/**
 * Created by Tlatoani on 1/13/18.
 */
public class VariableCollective implements Iterable<String> {
    private Map<String, ExpressionConstraints> constraintsMap = new HashMap<>();
    private Set<String> varyingOptions = new HashSet<>();
    private Set<String> allVariables = new HashSet<>();

    public void addExpression(String variable, ExpressionConstraints constraints) {
        ExpressionConstraints prevConstraints = getExpression(variable);
        if (prevConstraints != null) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            builder.addAll(prevConstraints.types);
            builder.addAll(constraints.types);
            ExpressionConstraints.LiteralState isLiteral = constraints.isLiteral == prevConstraints.isLiteral ? constraints.isLiteral : ExpressionConstraints.LiteralState.UNKNOWN;
            int time = constraints.time == prevConstraints.time ? constraints.time : 0;
            boolean nullable = constraints.nullable || prevConstraints.nullable;
            ExpressionConstraints newConstraints = new ExpressionConstraints(builder.build(), isLiteral, time, nullable);
            constraintsMap.put(variable, newConstraints);
        } else {
            constraintsMap.put(variable, constraints);
            allVariables.add(variable);
        }
    }

    public void addVaryingOption(String variable) {
        if (varyingOptions.add(variable)) {
            allVariables.add(variable);
        }
    }

    public ExpressionConstraints getExpression(String variable) {
        return constraintsMap.get(variable);
    }

    public boolean isVaryingOption(String variable) {
        return varyingOptions.contains(variable);
    }

    public int size() {
        return allVariables.size();
    }

    @Override
    public Iterator<String> iterator() {
        return allVariables.iterator();
    }

    @Override
    public void forEach(Consumer<? super String> action) {
        allVariables.forEach(action);
    }

    @Override
    public Spliterator<String> spliterator() {
        return allVariables.spliterator();
    }
}
