package com.sood.logo;

import com.example.market.grpc.LogoResponse;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class LogoService {

    private final LogoClient client;
    private final String apiKey;

    public LogoService(LogoClient client, @Value("${twelvedata.apikey}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    private String authHeader() {
        return "apikey " + apiKey;
    }

    public Mono<LogoResponse> getLogo(final String symbol) {
        return client.getLogo(authHeader(), symbol).map(s -> LogoResponse.newBuilder()
                .setUrl(s)
                .build());
    }
}
