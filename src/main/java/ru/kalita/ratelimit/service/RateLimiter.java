package ru.kalita.ratelimit.service;

public interface RateLimiter {

    boolean checkAvailability();
}
