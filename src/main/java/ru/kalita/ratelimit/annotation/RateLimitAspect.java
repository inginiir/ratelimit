package ru.kalita.ratelimit.annotation;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.kalita.ratelimit.exceptions.RateLimitExceededException;
import ru.kalita.ratelimit.service.RateLimiterService;
import ru.kalita.ratelimit.utils.RequestUtils;

@Aspect
@Component
public class RateLimitAspect {

    private final RateLimiterService rateLimiterService;

    @Autowired
    public RateLimitAspect(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Before(value = "@annotation(RateLimiting)")
    public void checkLimit() {
        String ip = RequestUtils.extractClientIpFromContext();
        if (limitPerIpExceeded(ip)) {
            throw new RateLimitExceededException(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
        }
    }

    private boolean limitPerIpExceeded(String ip) {
        return !rateLimiterService.checkLimitByIp(ip);
    }
}
