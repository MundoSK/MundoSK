package com.pie.tlatoani.Util.Collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * Created by Tlatoani on 1/12/18.
 */
public class ImmutableListCollector<T> implements Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> {
    @Override
    public Supplier<ImmutableList.Builder<T>> supplier() {
        return ImmutableList::builder;
    }

    @Override
    public BiConsumer<ImmutableList.Builder<T>, T> accumulator() {
        return ImmutableList.Builder::add;
    }

    @Override
    public BinaryOperator<ImmutableList.Builder<T>> combiner() {
        return (builder1, builder2) -> builder1.addAll(builder2.build());
    }

    @Override
    public Function<ImmutableList.Builder<T>, ImmutableList<T>> finisher() {
        return ImmutableList.Builder::build;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return ImmutableSet.of();
    }
}
