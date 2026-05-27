package com.xiaohongshu.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnBean(MeterRegistry.class)
public class MetricsFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(MetricsFilter.class);

    private final MeterRegistry meterRegistry;

    public MetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        String method = request.getMethod();
        String path = request.getRequestURI();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();

            // request total counter
            Counter.builder("http.requests.total")
                    .tag("method", method)
                    .tag("path", path)
                    .tag("status", String.valueOf(status))
                    .register(meterRegistry)
                    .increment();

            // request duration timer
            Timer.builder("http.requests.duration")
                    .tag("method", method)
                    .tag("path", path)
                    .register(meterRegistry)
                    .record(duration, TimeUnit.MILLISECONDS);

            // error counter
            if (status >= 400) {
                Counter.builder("http.requests.error")
                        .tag("method", method)
                        .tag("path", path)
                        .tag("status", String.valueOf(status))
                        .register(meterRegistry)
                        .increment();
            }

            log.debug("{} {} -> {} in {}ms", method, path, status, duration);
        }
    }
}
