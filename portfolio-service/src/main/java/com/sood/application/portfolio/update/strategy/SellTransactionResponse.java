package com.sood.application.portfolio.update.strategy;

import com.sood.infrastructure.entity.PortfolioItemEntity;

public record SellTransactionResponse(
        SellTransactionResult result,
        PortfolioItemEntity item
) {
}