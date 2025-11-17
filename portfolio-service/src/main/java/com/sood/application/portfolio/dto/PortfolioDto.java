package com.sood.application.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for Portfolio cache storage.
 * Contains complete portfolio information including items and history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioDto {

    private Long id;
    private String userId;
    private String portfolioName;
    private String description;
    private LocalDateTime lastUpdated;

    @Builder.Default
    private Set<PortfolioItemDto> items = new HashSet<>();

    @Builder.Default
    private Set<PortfolioHistoryDto> history = new HashSet<>();
}
