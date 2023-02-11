package ru.kalita.ratelimit.controllers.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import ru.kalita.ratelimit.service.BucketService;
import ru.kalita.ratelimit.utils.RequestUtils;

import java.io.IOException;

public class RateLimitFilter implements Filter {

    private final BucketService bucketService;

    @Autowired
    public RateLimitFilter(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String ip = RequestUtils.extractClientIp(httpServletRequest);

        if (!limitPerIpExceeded(ip)) {
            httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpServletResponse.getWriter().write(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase());
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean limitPerIpExceeded(String ip) {
        return bucketService.getBucketByIp(ip)
                .tryConsumeAndReturnRemaining(1)
                .isConsumed();
    }

    @Override
    public void destroy() {
    }
}
