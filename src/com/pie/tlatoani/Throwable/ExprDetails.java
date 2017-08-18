package com.pie.tlatoani.Throwable;

import com.pie.tlatoani.Util.MundoPropertyExpression;

/**
 * Created by Tlatoani on 8/18/17.
 */
public class ExprDetails extends MundoPropertyExpression<Throwable, String> {
    @Override
    public String convert(Throwable throwable) {
        return throwable.getMessage();
    }
}
