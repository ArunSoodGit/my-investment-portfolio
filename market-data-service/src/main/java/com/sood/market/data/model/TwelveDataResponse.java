package com.sood.market.data.model;


import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Serdeable
public class TwelveDataResponse {

    private String name;
    private String symbol;
    private String exchange;
    private String close;
    private String percent_change;

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public String getClose() {
        return close;
    }

    public String getPercent_change() {
        return percent_change;
    }
}
