package com.sood.portfolio;

import com.example.market.grpc.PortfolioRequest;
import com.example.market.grpc.PortfolioResponse;
import com.example.market.grpc.PortfolioServiceGrpc;
import io.micronaut.grpc.annotation.GrpcChannel;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioGrpcClient {

    private final PortfolioServiceGrpc.PortfolioServiceBlockingStub stub;

    public PortfolioGrpcClient(@GrpcChannel("portfolio") io.grpc.Channel channel) {
        this.stub = PortfolioServiceGrpc.newBlockingStub(channel);
    }

    public PortfolioResponse getPortfolio(final String userId, final String foundName) {
        final PortfolioRequest request = PortfolioRequest.newBuilder()
                .setUserId(userId)
                .setFoundName(foundName)
                .build();
        return stub.getPortfolio(request);
    }
}
