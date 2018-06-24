package com.pie.tlatoani.Tablist.General;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.google.common.collect.ImmutableList;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.event.Event;

import java.util.Optional;

public class ExprLineOfHeaderFooter extends SimpleExpression<String> {
    TablistProvider tablistProvider;
    private Optional<Expression<Number>> lineNumberExpression;
    boolean header;

    @Override
    protected String[] get(Event event) {
        Integer lineNumber = lineNumberExpression
                .map(expr -> Optional.ofNullable(expr.getSingle(event)))
                .orElse(Optional.of(-1))
                .map(Number::intValue)
                .orElse(null);
        if (lineNumber == null) {
            return new String[0];
        }
        return tablistProvider
                .view(event)
                .map(header ? Tablist::getHeader : Tablist::getFooter)
                .map(list -> list.get((lineNumber == -1 ? list.size() : lineNumber) - 1))
                .toArray(String[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString(lineNumberExpression.map(expr -> "line " + expr).orElse("last line")
                + " of tablist " + (header ? "header" : "footer") + " [of %]");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        lineNumberExpression = Optional.ofNullable((Expression<Number>) expressions[0]);
        tablistProvider = TablistProvider.of(expressions, 1);
        header = parseResult.mark == 0;
        return true;
    }

    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        String value = (delta == null || delta.length == 0 || delta[0] == null) ? "" : (String) delta[0];
        Integer lineNumber = lineNumberExpression
                .map(expr -> Optional.ofNullable(expr.getSingle(event)))
                .orElse(Optional.of(-1))
                .map(Number::intValue)
                .orElse(null);
        if (lineNumber == null) {
            return;
        }
        for (Tablist tablist : tablistProvider.get(event)) {
            ImmutableList<String> original = header ? tablist.getHeader() : tablist.getFooter();
            if (lineNumber <= original.size()) {
                int ix = (lineNumber == -1 ? original.size() : lineNumber) - 1;
                ImmutableList.Builder<String> builder = new ImmutableList.Builder<>();
                for (int i = 0; i < original.size(); i++) {
                    if (i == ix) {
                        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
                            builder.add(value);
                        } else if (mode == Changer.ChangeMode.ADD) {
                            builder.add(original.get(i) + value);
                        } //if mode == DELETE skip over the value
                    } else {
                        builder.add(original.get(i));
                    }
                }
                if (header) {
                    tablist.setHeader(builder.build());
                } else {
                    tablist.setFooter(builder.build());
                }
            } else if (!value.isEmpty()) {
                String[] modified = original.toArray(new String[lineNumber]);
                for (int i = original.size(); i < lineNumber - 1; i++) {
                    modified[i] = "";
                }
                modified[lineNumber - 1] = value;
                if (header) {
                    tablist.setHeader(ImmutableList.copyOf(modified));
                } else {
                    tablist.setFooter(ImmutableList.copyOf(modified));
                }
            }
        }
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD || mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(String.class);
        }
        return null;
    }
}
