package com.sood.market.data.client;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.rxjava3.core.Single;

@Client("${twelvedata.base-url}")
public interface TwelveDataClient {

    @Get("/quote")
    Single<String> getData(
            @QueryValue("apikey") String apiKey,
            @QueryValue("symbol") String symbol
    );
}
