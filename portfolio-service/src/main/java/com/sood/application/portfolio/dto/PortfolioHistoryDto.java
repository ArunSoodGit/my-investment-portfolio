package com.sood.application.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Portfolio History.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioHistoryDto {

    private Long id;
    private LocalDateTime date;
    private BigDecimal investedValue;
    private BigDecimal currentValue;
    
    @Builder.Default
    private List<SnapshotStockDataDto> stocksData = new ArrayList<>();
}
