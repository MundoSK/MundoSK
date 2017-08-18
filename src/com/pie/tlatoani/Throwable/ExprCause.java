package com.pie.tlatoani.Throwable;

import com.pie.tlatoani.Util.MundoPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprCause extends MundoPropertyExpression<Throwable, Throwable> {
    @Override
    public Throwable convert(Throwable throwable) {
        return throwable.getCause();
    }
}
