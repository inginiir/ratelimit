package ru.kalita.ratelimit.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kalita.ratelimit.service.ratelimiter.TokenBucket;
import ru.kalita.ratelimit.service.RateLimiterService;
import ru.kalita.ratelimit.utils.TimeUnit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    @Value("${rate.limit}")
    private long limitRate;
    @Value("${rate.period}")
    private long period;
    @Value("${rate.timeunit}")
    private String timeunit;

    private final Map<String, TokenBucket> cache = new ConcurrentHashMap<>();

    @Override
    public boolean checkLimitByIp(String ip) {
        return this.cache.computeIfAbsent(ip, newIp -> newBucket())
                .checkAvailability();
    }

    private TokenBucket newBucket() {
        TimeUnit timeUnit = TimeUnit.parseTimeUnit(this.timeunit);
        long periodNanos = timeUnit.getPeriodNanos(this.period);
        return new TokenBucket(this.limitRate, periodNanos);
    }
}
