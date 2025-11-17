package com.sood.application.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Portfolio Item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemDto {

    private Long id;
    private String symbol;
    private double quantity;
    private BigDecimal averagePurchasePrice;
    private BigDecimal investedValue;
    private LocalDate lastUpdated;
}
