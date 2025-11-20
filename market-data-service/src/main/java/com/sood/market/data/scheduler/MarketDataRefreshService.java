package com.sood.market.data.scheduler;

import jakarta.inject.Singleton;
import java.util.Set;

@Singleton
public class MarketDataRefreshService {

    private final Set<MarketAction> marketActions;

    public MarketDataRefreshService(final Set<MarketAction> marketActions) {
        this.marketActions = marketActions;
    }

    public void refresh() {
        marketActions.stream()
                .filter(MarketAction::shouldExecute)
                .forEach(MarketAction::execute);
    }
}

