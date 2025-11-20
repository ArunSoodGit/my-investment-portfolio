package com.sood.market.data.scheduler;

import com.sood.market.data.domain.MarketSession;

public interface MarketAction {
    boolean shouldExecute();

    void execute();
}
