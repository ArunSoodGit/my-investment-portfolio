package com.sood.historical.data.infrastructure.rest.dto;

import com.sood.historical.data.application.result.HistoricalDataResult;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Serdeable
public class HistoricalDataDTO {
    private final Long portfolioId;
    private final List<DataDTO> historicalData;

    public static HistoricalDataDTO from(final HistoricalDataResult result) {
        return HistoricalDataDTO.builder()
                .portfolioId(result.portfolioId())
                .historicalData(result.historicalDataList().stream()
                        .map(DataDTO::from)
                        .toList())
                .build();
    }
}
