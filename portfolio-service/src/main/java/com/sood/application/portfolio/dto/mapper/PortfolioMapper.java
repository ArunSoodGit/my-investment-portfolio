package com.sood.application.portfolio.dto.mapper;

import com.sood.application.portfolio.dto.PortfolioDto;
import com.sood.application.portfolio.dto.PortfolioItemDto;
import com.sood.application.portfolio.history.dto.PortfolioHistoryDto;
import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioHistoryEntity;
import com.sood.infrastructure.entity.PortfolioItemEntity;
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

        if (dto.getItems() != null) {
            entity.setItems(dto.getItems().stream()
                    .map(itemDto -> toItemEntity(itemDto, entity))
                    .collect(Collectors.toSet()));
        }

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
                .build();
    }

    private PortfolioHistoryEntity toHistoryEntity(final PortfolioHistoryDto dto, final PortfolioEntity portfolio) {
        final PortfolioHistoryEntity entity = new PortfolioHistoryEntity();
        entity.setId(dto.getId());
        entity.setDate(dto.getDate());
        entity.setInvestedValue(dto.getInvestedValue());
        entity.setCurrentValue(dto.getCurrentValue());
        entity.setPortfolio(portfolio);
        return entity;
    }
}
