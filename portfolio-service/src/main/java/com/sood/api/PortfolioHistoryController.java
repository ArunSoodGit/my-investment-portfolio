package com.sood.api;

import com.example.market.grpc.PortfolioHistoryResponse;
import com.example.market.grpc.PortfolioHistoryServiceGrpc;
import com.example.market.grpc.PortfolioRequest;
import com.sood.application.portfolio.history.PortfolioHistoryManager;
import io.grpc.stub.StreamObserver;
import io.micronaut.grpc.annotation.GrpcService;

/**
 * gRPC service controller for portfolio history retrieval.
 * Handles client requests for historical portfolio data and snapshots.
 */
@GrpcService
public class PortfolioHistoryController extends PortfolioHistoryServiceGrpc.PortfolioHistoryServiceImplBase {

    private final PortfolioHistoryManager historyManager;

    public PortfolioHistoryController(final PortfolioHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    /**
     * Retrieves portfolio history for a given portfolio ID.
     * Returns a single response containing all historical snapshots.
     *
     * @param request the portfolio request with portfolio ID
     * @param responseObserver the observer for sending the history response
     */
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
