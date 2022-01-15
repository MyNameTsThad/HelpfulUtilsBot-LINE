package io.github.mynametsthad.helpfulutilsbotline.core;

import java.util.Objects;
import java.util.Random;

public class Timer {
    private final long id;
    private String name;
    private long durationMillis;

    public Timer(String name, long durationMillis) {
        this.id = Math.abs(new Random().nextLong());
        this.name = name;
        this.durationMillis = durationMillis;
    }

    public long getId() {
        return id;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
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
        Timer timer = (Timer) o;
        return id == timer.id && durationMillis == timer.durationMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, durationMillis);
    }

    @Override
    public String toString() {
        return "Timer{" +
                "id=" + id +
                ", durationMillis=" + durationMillis +
                '}';
    }
}
