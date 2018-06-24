package com.pie.tlatoani.Tablist.General;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pie.tlatoani.Skin.Skin;
import com.pie.tlatoani.Tablist.Group.TablistProvider;
import com.pie.tlatoani.Tablist.Tablist;
import org.bukkit.event.Event;

/**
 * Created by Tlatoani on 4/7/18.
 */
public class ExprDefaultIcon extends SimpleExpression<Skin> {
    private TablistProvider tablistProvider;

    @Override
    protected Skin[] get(Event event) {
        return tablistProvider
                .view(event)
                .map(tablist -> tablist.getDefaultIcon().orElse(null))
                .toArray(Skin[]::new);
    }

    @Override
    public boolean isSingle() {
        return tablistProvider.isSingle();
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return tablistProvider.toString("default icon of [%'s] tablist");
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        tablistProvider = TablistProvider.of(expressions, 0);
        return true;
    }

    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Skin value = mode == Changer.ChangeMode.SET ? (Skin) delta[0] : null;
        for (Tablist tablist : tablistProvider.get(event)) {
            tablist.setDefaultIcon(value);
        }
    }

    public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.RESET) {
            return CollectionUtils.array(Skin.class);
        }
        return null;
    }
}
