package ru.kalita.ratelimit.utils;

import java.time.Duration;
import java.util.Arrays;

public enum TimeUnit {

    SECONDS {
        @Override
        public Duration getDuration(long amount) {
            return Duration.ofSeconds(amount);
        }
    },
    MINUTES {
        @Override
        public Duration getDuration(long amount) {
            return Duration.ofMinutes(amount);
        }
    },
    HOURS {
        @Override
        public Duration getDuration(long amount) {
            return Duration.ofHours(amount);
        }
    };

    public static TimeUnit parseTimeUnit(String timeUnit) {
        return Arrays.stream(TimeUnit.values())
                .filter(refillStrategy -> refillStrategy.name().equals(timeUnit))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Введена неподдерживаемая единица времени: " + timeUnit));
    }

    public abstract Duration getDuration(long amount);

}
