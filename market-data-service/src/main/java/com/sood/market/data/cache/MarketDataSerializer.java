package com.sood.market.data.cache;

import com.example.market.grpc.MarketDataResponse;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.sood.market.data.exception.CacheSerializationException;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

/**
 * Handles serialization and deserialization of MarketDataResponse for caching.
 * Uses Protocol Buffers JSON format for Redis storage.
 */
@Singleton
@Log4j2
public class MarketDataSerializer {

    private final JsonFormat.Printer printer;
    private final JsonFormat.Parser parser;

    public MarketDataSerializer() {
        this.printer = JsonFormat.printer().alwaysPrintFieldsWithNoPresence();
        this.parser = JsonFormat.parser().ignoringUnknownFields();
    }

    /**
     * Serializes MarketDataResponse to JSON string.
     *
     * @param data the market data to serialize
     * @return JSON string representation
     * @throws CacheSerializationException if serialization fails
     */
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

    /**
     * Deserializes JSON string to MarketDataResponse.
     *
     * @param json the JSON string
     * @return deserialized MarketDataResponse
     * @throws CacheSerializationException if deserialization fails
     */
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
