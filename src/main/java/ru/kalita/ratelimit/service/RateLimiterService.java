package ru.kalita.ratelimit.service;

public interface RateLimiterService {

    boolean checkLimitByIp(String ip);
}
