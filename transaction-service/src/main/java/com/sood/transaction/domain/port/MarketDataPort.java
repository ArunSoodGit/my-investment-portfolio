package com.sood.transaction.domain.port;

import com.example.market.grpc.MarketDataResponse;
import io.reactivex.rxjava3.core.Single;

public interface MarketDataPort {
    Single<MarketDataResponse> getMarketData(String symbol);
}
