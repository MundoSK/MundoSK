package com.pie.tlatoani.Miscellaneous.MiscBukkit;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

/**
 * Created by Tlatoani on 4/20/17.
 */
public class ExprNewPortal extends SimpleExpression<Location> {
    private Expression<Number> radius;
    private Expression<Location> targetLoc;

    public static Location createPortal(Location targetLoc, int radius, TravelAgent travelAgent) {
        travelAgent.setCanCreatePortal(true);
        travelAgent.setCreationRadius(radius);
        travelAgent.setSearchRadius(radius);
        if (travelAgent.createPortal(targetLoc)) {
            return travelAgent.findPortal(targetLoc);
        } else {
            return null;
        }
    }

    @Override
    protected Location[] get(Event event) {
        if (event instanceof PlayerPortalEvent) {
            return new Location[]{createPortal(targetLoc.getSingle(event), radius.getSingle(event).intValue(), ((PlayerPortalEvent) event).getPortalTravelAgent())};
        } else if (event instanceof EntityPortalEvent) {
            return new Location[]{createPortal(targetLoc.getSingle(event), radius.getSingle(event).intValue(), ((EntityPortalEvent) event).getPortalTravelAgent())};
        }
        throw new IllegalArgumentException("The event " + event + " should be a PlayerPortalEvent or EntityPortalEvent");

    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Location> getReturnType() {
        return Location.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "new nether portal within " + radius + " blocks of " + targetLoc;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        if (!ScriptLoader.isCurrentEvent(PlayerPortalEvent.class, EntityPortalEvent.class)) {
            Skript.error("'new nether portal' can only be used in an 'on teleport' event!");
            return false;
        }
        radius = (Expression<Number>) expressions[0];
        targetLoc = (Expression<Location>) expressions[1];
        return false;
    }
}
