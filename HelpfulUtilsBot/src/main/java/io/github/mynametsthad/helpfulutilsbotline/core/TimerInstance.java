package io.github.mynametsthad.helpfulutilsbotline.core;

import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class TimerInstance {
    private long id;

    private String name;
    private final long parentTimerId;
    private long startTime;
    private long timeLeft;
    private boolean isPaused;

    public TimerInstance(String name, Timer parentTimer, boolean isPaused) {
        this.id = Math.abs(new Random().nextLong());
        this.name = name;
        this.parentTimerId = parentTimer.getId();
        this.startTime = new Date().getTime();
        this.timeLeft = parentTimer.getDurationMillis();
        this.isPaused = isPaused;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getParentTimerId() {
        return parentTimerId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimerInstance that = (TimerInstance) o;
        return id == that.id && parentTimerId == that.parentTimerId && startTime == that.startTime && timeLeft == that.timeLeft && isPaused == that.isPaused;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, parentTimerId, startTime, timeLeft, isPaused);
    }

    @Override
    public String toString() {
        return "TimerInstance{" +
                "id=" + id +
                ", parentTimerId=" + parentTimerId +
                ", startTime=" + startTime +
                ", timeLeft=" + timeLeft +
                ", isPaused=" + isPaused +
                '}';
    }
}
