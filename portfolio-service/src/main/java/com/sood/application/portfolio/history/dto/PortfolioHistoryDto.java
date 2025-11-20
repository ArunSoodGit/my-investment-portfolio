package com.sood.application.portfolio.history.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
