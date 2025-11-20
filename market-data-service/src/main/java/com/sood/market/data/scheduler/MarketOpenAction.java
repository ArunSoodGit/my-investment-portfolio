package com.sood.market.data.scheduler;

import com.sood.market.data.domain.MarketSession;
import com.sood.market.data.service.MarketDataUpdateService;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class MarketOpenAction implements MarketAction {

    private final MarketDataUpdateService updateService;
    private final MarketSession session;

    public MarketOpenAction(final MarketDataUpdateService updateService, final MarketSession session) {
        this.updateService = updateService;
        this.session = session;
    }

    @Override
    public boolean shouldExecute() {
        return session.isMarketOpen();
    }

    @Override
    public void execute() {
        updateService.refreshAllSymbols().subscribe();
    }
}
