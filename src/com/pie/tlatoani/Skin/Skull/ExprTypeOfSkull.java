package com.pie.tlatoani.Skin.Skull;

import ch.njol.skript.classes.Changer;
import com.pie.tlatoani.Core.Skript.ChangeablePropertyExpression;
import org.bukkit.SkullType;

/**
 * Created by Tlatoani on 4/14/18.
 */
public class ExprTypeOfSkull extends ChangeablePropertyExpression<Object, SkullType> {
    @Override
    public void change(Object o, SkullType skullType, Changer.ChangeMode changeMode) {
        ExprOwnerOfSkull.makeSkullUtil(o).ifPresent(skull -> skull.setSkullType(skullType));
    }

    @Override
    public Changer.ChangeMode[] getChangeModes() {
        return new Changer.ChangeMode[]{Changer.ChangeMode.SET};
    }

    @Override
    public SkullType convert(Object o) {
        return ExprOwnerOfSkull.getSkullUtil(o).map(SkullUtil::getSkullType).orElse(null);
    }
}
