package ru.kalita.ratelimit.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kalita.ratelimit.controllers.filters.RateLimitFilter;
import ru.kalita.ratelimit.service.BucketService;

@Configuration
public class FilterConfig {

    private final BucketService bucketService;

    @Autowired
    public FilterConfig(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> filterRegistrationBean() {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        RateLimitFilter customURLFilter = new RateLimitFilter(bucketService);
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/api/limitedResource");
        return registrationBean;
    }
}
