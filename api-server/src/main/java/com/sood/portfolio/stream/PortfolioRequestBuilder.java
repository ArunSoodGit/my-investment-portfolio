package com.sood.portfolio.stream;

import com.example.market.grpc.PortfolioRequest;

public class PortfolioRequestBuilder {

    public PortfolioRequest build(final Long portfolioId) {
        return PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();
    }
}
