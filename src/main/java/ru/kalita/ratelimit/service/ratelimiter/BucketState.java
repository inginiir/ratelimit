package ru.kalita.ratelimit.service.ratelimiter;

public class BucketState {

    private long availableTokens;
    private long lastRefillTimeStamp;

    public long getAvailableTokens() {
        return availableTokens;
    }

    public void setAvailableTokens(long availableTokens) {
        this.availableTokens = availableTokens;
    }

    public long getLastRefillTimeStamp() {
        return lastRefillTimeStamp;
    }

    public void setLastRefillTimeStamp(long lastRefillTimeStamp) {
        this.lastRefillTimeStamp = lastRefillTimeStamp;
    }
}
