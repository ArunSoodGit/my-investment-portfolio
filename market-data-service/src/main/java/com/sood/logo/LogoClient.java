package com.sood.logo;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("${twelvedata.base-url}")
public interface LogoClient {

    @Get("/logo")
    Mono<String> getLogo(
            @Header(HttpHeaders.AUTHORIZATION) String authHeader,
            @QueryValue("symbol") String symbol
    );
}
