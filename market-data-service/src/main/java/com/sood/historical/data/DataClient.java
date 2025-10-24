package com.sood.historical.data;

import com.sood.historical.data.model.TwelveDataResponse;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import reactor.core.publisher.Mono;

@Client("${twelvedata.base-url}")
public interface DataClient {

    @Get("/time_series")
    Mono<TwelveDataResponse> getData(
            @Header(HttpHeaders.AUTHORIZATION) String authHeader,
            @QueryValue("symbol") String symbol,
            @QueryValue("interval") String interval
    );
}
