package ru.kalita.ratelimit.service.impl;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kalita.ratelimit.service.BucketService;
import ru.kalita.ratelimit.utils.RefillStrategy;
import ru.kalita.ratelimit.utils.TimeUnit;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BucketServiceImpl implements BucketService {

    @Value("${rate.limit}")
    private long limitRate;
    @Value("${rate.period}")
    private long period;
    @Value("${rate.refill}")
    private String refillType;
    @Value("${rate.timeunit}")
    private String timeunit;

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public Bucket getBucketByIp(String ip) {
        return cache.computeIfAbsent(ip, newIp -> newBucket());
    }

    private Bucket newBucket() {
        RefillStrategy refillStrategy = RefillStrategy.parseStrategy(refillType);
        TimeUnit timeUnit = TimeUnit.parseStrategy(timeunit);
        Duration duration = timeUnit.getDuration(period);
        Bandwidth bandwidth = refillStrategy.getBandwidth(limitRate, duration);
        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }
}
