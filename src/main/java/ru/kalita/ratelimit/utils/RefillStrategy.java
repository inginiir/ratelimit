package ru.kalita.ratelimit.utils;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.Arrays;

public enum RefillStrategy {

    GREED {
        @Override
        public Bandwidth getBandwidth(long limitRate, Duration period) {
            return Bandwidth.classic(limitRate, Refill.greedy(limitRate, period));
        }
    },
    INTERVAL {
        @Override
        public Bandwidth getBandwidth(long limitRate, Duration period) {
            return Bandwidth.classic(limitRate, Refill.intervally(limitRate, period));
        }
    };

    public static RefillStrategy parseStrategy(String refillType) {
        return Arrays.stream(RefillStrategy.values())
                .filter(refillStrategy -> refillStrategy.name().equals(refillType))
                .findFirst()
                .orElse(GREED);
    }

    public abstract Bandwidth getBandwidth(long limitRate, Duration period);
}
