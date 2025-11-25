package com.sood.historical.data.infrastructure.rest.dto;

import com.sood.historical.data.application.result.HistoricalData;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Serdeable
public class DataDTO {

    private final String date;
    private final BigDecimal totalInvested;
    private final BigDecimal totalCurrentValue;

    public static DataDTO from(final HistoricalData data) {
        return DataDTO.builder()
                .date(data.date())
                .totalCurrentValue(data.totalCurrentValue())
                .totalInvested(data.totalInvested())
                .build();
    }
}
