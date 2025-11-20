package com.sood.api;

import com.example.market.grpc.PortfolioHistoryResponse;
import com.example.market.grpc.PortfolioHistoryServiceGrpc;
import com.example.market.grpc.PortfolioRequest;
import com.sood.application.portfolio.history.PortfolioHistoryManager;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;

@GrpcService
public class PortfolioHistoryController extends PortfolioHistoryServiceGrpc.PortfolioHistoryServiceImplBase {

    private final PortfolioHistoryManager historyManager;

    public PortfolioHistoryController(final PortfolioHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void getPortfolioHistory(final PortfolioRequest request, final StreamObserver<PortfolioHistoryResponse> responseObserver) {
        try {
            final PortfolioHistoryResponse response = historyManager.get(request.getPortfolioId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
