package ru.kalita.ratelimit.utils;

import java.time.Duration;
import java.util.Arrays;

public enum TimeUnit {

    SECONDS {
        @Override
        public long getPeriodNanos(long amount) {
            return Duration.ofSeconds(amount).toNanos();
        }
    },
    MINUTES {
        @Override
        public long getPeriodNanos(long amount) {
            return Duration.ofMinutes(amount).toNanos();
        }
    },
    HOURS {
        @Override
        public long getPeriodNanos(long amount) {
            return Duration.ofHours(amount).toNanos();
        }
    };

    public static TimeUnit parseTimeUnit(String timeUnit) {
        return Arrays.stream(TimeUnit.values())
                .filter(refillStrategy -> refillStrategy.name().equals(timeUnit))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Specified incorrect TimeUnit property: " + timeUnit));
    }

    public abstract long getPeriodNanos(long amount);
}
