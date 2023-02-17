package ru.kalita.ratelimit.service.ratelimiter;

import ru.kalita.ratelimit.service.RateLimiter;

import java.util.concurrent.atomic.AtomicReference;

public class TokenBucket implements RateLimiter {

    private final long bucketCapacity;
    private final long nanosToAddToken;
    private final AtomicReference<BucketState> bucketStateReference;

    public TokenBucket(long bucketCapacity, long refreshPeriod) {
        this.bucketCapacity = bucketCapacity;
        this.nanosToAddToken = refreshPeriod / bucketCapacity;

        BucketState initialState = new BucketState();
        initialState.setLastRefillTimeStamp(System.nanoTime());
        initialState.setAvailableTokens(bucketCapacity);
        this.bucketStateReference = new AtomicReference<>(initialState);
    }

    public boolean checkAvailability() {
        BucketState newState = new BucketState();
        long now = System.nanoTime();
        while (true) {
            BucketState previousState = bucketStateReference.get();
            newState.setAvailableTokens(previousState.getAvailableTokens());
            newState.setLastRefillTimeStamp(previousState.getLastRefillTimeStamp());
            refillBucket(newState, now);
            long availableTokens = newState.getAvailableTokens();
            if (availableTokens < 1) {
                return false;
            }
            newState.setAvailableTokens(--availableTokens);
            if (bucketStateReference.compareAndSet(previousState, newState)) {
                return true;
            }
        }
    }

    private void refillBucket(BucketState newState, long now) {
        long lastRefillTimeStamp = newState.getLastRefillTimeStamp();
        long elapsedTime = now - lastRefillTimeStamp;
        if (elapsedTime <= nanosToAddToken) {
            return;
        }
        long tokensToBeAdded = elapsedTime / nanosToAddToken;
        newState.setAvailableTokens(Math.min(bucketCapacity, newState.getAvailableTokens() + tokensToBeAdded));
        newState.setLastRefillTimeStamp(lastRefillTimeStamp + tokensToBeAdded * nanosToAddToken);
    }
}
