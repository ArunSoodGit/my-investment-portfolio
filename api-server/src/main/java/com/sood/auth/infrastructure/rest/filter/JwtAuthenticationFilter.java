package com.sood.auth.infrastructure.rest.filter;

import com.sood.auth.application.AuthApplicationService;
import com.sood.auth.application.result.TokenValidationResult;
import io.micronaut.core.order.Ordered;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.rxjava3.core.Flowable;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;

@Filter("/v1/api/**")
@Log4j2
public class JwtAuthenticationFilter implements HttpServerFilter, Ordered {

    private final AuthApplicationService authApplicationService;
    private final AuthExclusionList exclusionList;

    public JwtAuthenticationFilter(final AuthApplicationService authApplicationService, final AuthExclusionList exclusionList) {
        this.authApplicationService = authApplicationService;
        this.exclusionList = exclusionList;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(final HttpRequest<?> request, final ServerFilterChain chain) {
        final String path = request.getPath();
        final String method = request.getMethod().toString();

        if (exclusionList.isExcluded(method, path)) {
            log.debug("Skipping auth check for: {} {}", method, path);
            return chain.proceed(request);
        }

        final Optional<String> authHeader = Optional.ofNullable(request.getHeaders().get("Authorization"));

        if (authHeader.isEmpty()) {
            log.warn("Missing Authorization header for: {} {}", method, path);
            return Flowable.just(HttpResponse.unauthorized()
                    .body("Missing Authorization header"));
        }

        final String token = extractToken(authHeader.get());
        if (token == null) {
            log.warn("Invalid Authorization header format for: {} {}", method, path);
            return Flowable.just(HttpResponse.unauthorized()
                    .body("Invalid Authorization header format"));
        }

        final TokenValidationResult validation = authApplicationService.validateToken(token);

        if (!validation.valid()) {
            log.warn("Token validation failed: {}", validation.error());
            return Flowable.just(HttpResponse.unauthorized()
                    .body("Token validation failed: " + validation.error()));
        }

        return chain.proceed(request);
    }

    @Override
    public int getOrder() {
        return 2;
    }

    private String extractToken(final String authHeader) {
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
