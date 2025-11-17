package com.sood.application.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Snapshot Stock Data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotStockDataDto {

    private Long id;
    private LocalDateTime createdAt;
    private String symbol;
    private String currentPrice;
    private String percentageChange;
    private String companyName;
    private String exchange;
}
