package com.sood;

import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;

import static io.micronaut.http.annotation.Filter.MATCH_ALL_PATTERN;

@Singleton
@Filter(MATCH_ALL_PATTERN)
public class GlobalCorsFilter implements HttpServerFilter {

    private static final String ORIGIN = "http://localhost:3000";

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(final HttpRequest<?> request, final ServerFilterChain chain) {
        System.out.println("GlobalCorsFilter - żądanie: " + request.getMethod() + " " + request.getUri());

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return Flowable.just(
                    HttpResponse.ok()
                            .header("Access-Control-Allow-Origin", ORIGIN)
                            .header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS")
                            .header("Access-Control-Allow-Headers", "Content-Type,Authorization")
                            .header("Access-Control-Allow-Credentials", "true")
                            .header("Access-Control-Max-Age", "3600")
            );
        }

        // dla normalnych requestów
        return Flowable.fromPublisher(chain.proceed(request))
                .map(response -> response
                        .header("Access-Control-Allow-Origin", ORIGIN)
                        .header("Access-Control-Allow-Credentials", "true")
                );
    }
}