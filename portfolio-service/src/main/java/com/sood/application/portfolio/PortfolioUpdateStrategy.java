package com.sood.application.portfolio;

import com.sood.infrastructure.entity.PortfolioEntity;
import sood.found.TransactionCreatedEvent;

public interface PortfolioUpdateStrategy {

    void update(PortfolioEntity portfolio, TransactionCreatedEvent event);
}
