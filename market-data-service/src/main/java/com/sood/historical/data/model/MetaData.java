package com.sood.historical.data.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Builder
@Getter
@Setter
@Serdeable
public class MetaData {

    private final String symbol;
    private final String interval;
    private final String currency;
    private final String exchange_timezone;
    private final String exchange;
    private final String mic_code;
    private final String type;
}
