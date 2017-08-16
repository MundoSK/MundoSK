package com.pie.tlatoani.WorldBorder.BorderEvent;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.Optional;

/**
 * Created by Tlatoani on 8/16/17.
 */
public class WorldBorderImpl implements WorldBorder {
    public final World world;
    protected final WorldBorder border;

    private Optional<BorderStabilizeEvent.Caller> caller = Optional.empty();
    private Optional<Double> originalDiameter = Optional.empty();
    private Optional<Double> eventualDiameter = Optional.empty();
    private Optional<Double> totalTimeInSeconds = Optional.empty();

    public WorldBorderImpl(World world) {
        this.world = world;
        this.border = world.getWorldBorder();
    }

    void onStabilize() {
        caller = Optional.empty();
        originalDiameter = Optional.empty();
        eventualDiameter = Optional.empty();
        totalTimeInSeconds = Optional.empty();
    }

    private void onSetSizeOverTime(double newDiameter, long seconds) {
        this.caller.ifPresent(BorderStabilizeEvent.Caller::invalidate);
        BorderStabilizeEvent.Caller caller = new BorderStabilizeEvent.Caller(world, this);
        caller.schedule((int) seconds * 20);
        this.caller = Optional.of(caller);
        originalDiameter = Optional.of(getSize());
        eventualDiameter = Optional.of(newDiameter);
        totalTimeInSeconds = Optional.of((double) seconds);
    }

    public double getOriginalDiameter() {
        return originalDiameter.orElse(getSize());
    }

    public double getEventualDiameter() {
        return eventualDiameter.orElse(getSize());
    }

    public boolean isMoving() {
        return caller.isPresent();
    }

    public double getRemainingDistance() {
        if (!isMoving()) {
            return 0;
        }
        return Math.abs(getEventualDiameter() - getOriginalDiameter());
    }

    public double remainingTimeInSeconds() {
        if (!isMoving()) {
            return 0;
        }
        return totalTimeInSeconds.get() * (getRemainingDistance() / Math.abs(getEventualDiameter() - getOriginalDiameter()));
    }

    @Override
    public void reset() {
        border.reset();
    }

    @Override
    public double getSize() {
        return border.getSize();
    }

    @Override
    public void setSize(double v) {
        border.setSize(v);
    }

    @Override
    public void setSize(double v, long l) {
        onSetSizeOverTime(v, l);
        border.setSize(v, l);
    }

    @Override
    public Location getCenter() {
        return border.getCenter();
    }

    @Override
    public void setCenter(double v, double v1) {
        border.setCenter(v, v1);
    }

    @Override
    public void setCenter(Location location) {
        border.setCenter(location);
    }

    @Override
    public double getDamageBuffer() {
        return border.getDamageBuffer();
    }

    @Override
    public void setDamageBuffer(double v) {
        border.setDamageBuffer(v);
    }

    @Override
    public double getDamageAmount() {
        return border.getDamageAmount();
    }

    @Override
    public void setDamageAmount(double v) {
        border.setDamageAmount(v);
    }

    @Override
    public int getWarningTime() {
        return border.getWarningTime();
    }

    @Override
    public void setWarningTime(int i) {
        border.setWarningTime(i);
    }

    @Override
    public int getWarningDistance() {
        return border.getWarningDistance();
    }

    @Override
    public void setWarningDistance(int i) {
        border.setWarningDistance(i);
    }

    public boolean isInside(Location location) {
        throw new UnsupportedOperationException("This version of Bukkit/Spigot does not support the isInside(Location) method");
    }
}
