package de.zebrajaeger.sphere2cube.utils;

public class Stopwatch {
    private long startTime;
    private Long stopTime;

    public static Stopwatch fromNow() {
        return new Stopwatch(System.currentTimeMillis());
    }

    private Stopwatch(long startTime) {
        this.startTime = startTime;
    }

    public Stopwatch stop() {
        if (stopTime != null) {
            throw new IllegalStateException("Stopwatch has already been stopped");
        }
        stopTime = System.currentTimeMillis();
        return this;
    }

    public long getMillis() {
        if (stopTime == null) {
            throw new IllegalStateException("Stopwatch is running. This Operation is available after stop.");
        }
        return stopTime - startTime;
    }

    public String toHumanReadable() {
        return Utils.durationToString(getMillis());
    }
}
