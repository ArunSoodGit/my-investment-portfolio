package com.sood.application.portfolio.dto.mapper;

import com.sood.application.portfolio.dto.PortfolioDto;
import com.sood.application.portfolio.dto.PortfolioItemDto;
import com.sood.application.portfolio.history.dto.PortfolioHistoryDto;
import com.sood.application.portfolio.history.dto.SnapshotStockDataDto;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioHistoryEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
import com.sood.infrastructure.entity.SnapshotStockData;
import jakarta.inject.Singleton;
import java.util.stream.Collectors;

/**
 * Mapper for converting between PortfolioEntity and PortfolioDto.
 */
@Singleton
public class PortfolioMapper {

    /**
     * Converts PortfolioEntity to PortfolioDto including items and history.
     *
     * @param entity the portfolio entity
     * @return the portfolio DTO
     */
    public PortfolioDto toDto(final PortfolioEntity entity) {
        if (entity == null) {
            return null;
        }

        return PortfolioDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .portfolioName(entity.getPortfolioName())
                .description(entity.getDescription())
                .lastUpdated(entity.getLastUpdated())
                .items(entity.getItems().stream()
                        .map(this::toItemDto)
                        .collect(Collectors.toSet()))
                .history(entity.getHistory().stream()
                        .map(this::toHistoryDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * Converts PortfolioDto to PortfolioEntity including items and history.
     *
     * @param dto the portfolio DTO
     * @return the portfolio entity
     */
    public PortfolioEntity toEntity(final PortfolioDto dto) {
        if (dto == null) {
            return null;
        }

        final PortfolioEntity entity = new PortfolioEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setPortfolioName(dto.getPortfolioName());
        entity.setDescription(dto.getDescription());
        entity.setLastUpdated(dto.getLastUpdated());

        // Convert items
        if (dto.getItems() != null) {
            entity.setItems(dto.getItems().stream()
                    .map(itemDto -> toItemEntity(itemDto, entity))
                    .collect(Collectors.toSet()));
        }

        // Convert history
        if (dto.getHistory() != null) {
            entity.setHistory(dto.getHistory().stream()
                    .map(historyDto -> toHistoryEntity(historyDto, entity))
                    .collect(Collectors.toSet()));
        }

        return entity;
    }

    private PortfolioItemDto toItemDto(final PortfolioItemEntity entity) {
        return PortfolioItemDto.builder()
                .id(entity.getId())
                .symbol(entity.getSymbol())
                .quantity(entity.getQuantity())
                .averagePurchasePrice(entity.getAveragePurchasePrice())
                .investedValue(entity.getInvestedValue())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }

    private PortfolioItemEntity toItemEntity(final PortfolioItemDto dto, final PortfolioEntity portfolio) {
        final PortfolioItemEntity entity = new PortfolioItemEntity();
        entity.setId(dto.getId());
        entity.setSymbol(dto.getSymbol());
        entity.setQuantity(dto.getQuantity());
        entity.setAveragePurchasePrice(dto.getAveragePurchasePrice());
        entity.setInvestedValue(dto.getInvestedValue());
        entity.setLastUpdated(dto.getLastUpdated());
        entity.setPortfolio(portfolio);
        return entity;
    }

    private PortfolioHistoryDto toHistoryDto(final PortfolioHistoryEntity entity) {
        return PortfolioHistoryDto.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .investedValue(entity.getInvestedValue())
                .currentValue(entity.getCurrentValue())
                .stocksData(entity.getStocksData().stream()
                        .map(this::toSnapshotDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private PortfolioHistoryEntity toHistoryEntity(final PortfolioHistoryDto dto, final PortfolioEntity portfolio) {
        final PortfolioHistoryEntity entity = new PortfolioHistoryEntity();
        entity.setId(dto.getId());
        entity.setDate(dto.getDate());
        entity.setInvestedValue(dto.getInvestedValue());
        entity.setCurrentValue(dto.getCurrentValue());
        entity.setPortfolio(portfolio);

        if (dto.getStocksData() != null) {
            entity.setStocksData(dto.getStocksData().stream()
                    .map(snapshotDto -> toSnapshotEntity(snapshotDto, entity))
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private SnapshotStockDataDto toSnapshotDto(final SnapshotStockData entity) {
        return SnapshotStockDataDto.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .symbol(entity.getSymbol())
                .currentPrice(entity.getCurrentPrice())
                .percentageChange(entity.getPercentageChange())
                .companyName(entity.getCompanyName())
                .exchange(entity.getExchange())
                .build();
    }

    private SnapshotStockData toSnapshotEntity(final SnapshotStockDataDto dto, final PortfolioHistoryEntity history) {
        final SnapshotStockData entity = new SnapshotStockData();
        entity.setId(dto.getId());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setSymbol(dto.getSymbol());
        entity.setCurrentPrice(dto.getCurrentPrice());
        entity.setPercentageChange(dto.getPercentageChange());
        entity.setCompanyName(dto.getCompanyName());
        entity.setExchange(dto.getExchange());
        entity.setPortfolioHistory(history);
        return entity;
    }
}
