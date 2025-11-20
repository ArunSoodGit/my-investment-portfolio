package com.sood.market.data.cache;

import com.example.market.grpc.MarketDataResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.sood.market.data.exception.CacheSerializationException;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class MarketDataSerializer {

    private final JsonFormat.Printer printer;
    private final JsonFormat.Parser parser;

    public MarketDataSerializer() {
        this.printer = JsonFormat.printer().alwaysPrintFieldsWithNoPresence();
        this.parser = JsonFormat.parser().ignoringUnknownFields();
    }

    public String serialize(final MarketDataResponse data) {
        if (data == null) {
            throw new IllegalArgumentException("MarketDataResponse cannot be null");
        }

        try {
            return printer.print(data);
        } catch (InvalidProtocolBufferException e) {
            throw new CacheSerializationException(
                    "Failed to serialize market data for symbol: " + data.getSymbol(), e);
        }
    }

    public MarketDataResponse deserialize(final String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            final MarketDataResponse.Builder builder = MarketDataResponse.newBuilder();
            parser.merge(json, builder);
            return builder.build();
        } catch (InvalidProtocolBufferException e) {
            throw new CacheSerializationException("Failed to deserialize market data from JSON", e);
        }
    }
}
