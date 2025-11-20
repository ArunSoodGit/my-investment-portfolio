package com.sood.market.data.scheduler;

public interface MarketAction {
    boolean shouldExecute();

    void execute();
}
