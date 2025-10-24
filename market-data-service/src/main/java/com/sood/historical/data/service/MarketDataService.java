package com.sood.historical.data.service;

import com.example.market.grpc.DataResponse;
import com.example.market.grpc.MetaData;
import com.example.market.grpc.PriceEntry;
import com.sood.historical.data.DataClient;
import com.sood.historical.data.model.TwelveDataResponse;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

@Singleton
public class MarketDataService {

    private final DataClient client;
    private final String apiKey;

    public MarketDataService(DataClient client, @Value("${twelvedata.apikey}") String apiKey) {
        this.client = client;
        this.apiKey = apiKey;
    }

    private String authHeader() {
        return "apikey " + apiKey;
    }

    public Mono<DataResponse> getData(final String symbol, final String interval) {
        return client.getData(authHeader(), symbol, interval)
                .map(this::mapToGrpc);
    }

    private DataResponse mapToGrpc(final TwelveDataResponse twelveDataResponse) {
        var meta = MetaData.newBuilder()
                .setSymbol(twelveDataResponse.getMeta().getSymbol())
                .setInterval(twelveDataResponse.getMeta().getInterval())
                .setCurrency(twelveDataResponse.getMeta().getCurrency())
                .setExchangeTimezone(twelveDataResponse.getMeta().getExchange_timezone())
                .setExchange(twelveDataResponse.getMeta().getExchange())
                .setMicCode(twelveDataResponse.getMeta().getMic_code())
                .setType(twelveDataResponse.getMeta().getType())
                .build();

        var builder = DataResponse.newBuilder().setMeta(meta).setStatus(twelveDataResponse.getStatus());

        twelveDataResponse.getValues().forEach(v -> builder.addValues(PriceEntry.newBuilder()
                .setDatetime(v.getDatetime())
                .setOpen(v.getOpen())
                .setHigh(v.getHigh())
                .setLow(v.getLow())
                .setClose(v.getClose())
                .setVolume(v.getVolume())
                .build()));

        return builder.build();
    }
}
