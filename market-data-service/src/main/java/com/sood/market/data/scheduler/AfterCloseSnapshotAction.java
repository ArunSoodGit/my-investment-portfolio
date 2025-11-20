package com.sood.market.data.scheduler;

import com.sood.market.data.domain.MarketSession;
import com.sood.market.data.service.MarketDataService;
import jakarta.inject.Singleton;
import lombok.extern.log4j.Log4j2;

@Singleton
@Log4j2
public class AfterCloseSnapshotAction implements MarketAction {

    private final MarketSession session;
    private final MarketDataService dataService;
    private final SnapshotManager snapshotManager;

    public AfterCloseSnapshotAction(final MarketSession session, final MarketDataService dataService,
            final SnapshotManager snapshotManager) {
        this.session = session;
        this.dataService = dataService;
        this.snapshotManager = snapshotManager;
    }

    @Override
    public boolean shouldExecute() {
        return session.isAfterMarketClose() && snapshotManager.shouldSaveSnapshot();
    }

    @Override
    public void execute() {
        dataService.refreshMarketDataInDatabase();
        snapshotManager.markSnapshotSaved();
    }
}
