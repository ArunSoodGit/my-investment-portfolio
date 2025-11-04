package com.sood.portfolio.history;

import com.example.market.grpc.PortfolioHistoryResponse;
import com.example.market.grpc.PortfolioHistoryServiceGrpc;
import com.example.market.grpc.PortfolioRequest;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioHistoryGrpcClient {

    private final PortfolioHistoryServiceGrpc.PortfolioHistoryServiceBlockingStub stub;

    public PortfolioHistoryGrpcClient(@GrpcChannel("portfolio") final io.grpc.Channel channel) {
        this.stub = PortfolioHistoryServiceGrpc.newBlockingStub(channel);
    }

    public PortfolioHistoryResponse getPortfolioHistory(final Long portfolioId) {
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setPortfolioId(portfolioId)
                .build();

        return stub.getPortfolioHistory(request);
    }
}
