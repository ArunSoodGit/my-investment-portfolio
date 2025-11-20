package com.sood.portfolio.stream;

import com.sood.portfolio.model.PortfolioResponseDTO;
import io.reactivex.rxjava3.core.Observable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioStreamService {

    private final PortfolioGrpcStreamClient portfolioClient;

    @Inject
    public PortfolioStreamService(final PortfolioGrpcStreamClient portfolioClient) {
        this.portfolioClient = portfolioClient;
    }

    public Observable<PortfolioResponseDTO> stream(final Long portfolioId) {
        return portfolioClient.stream(portfolioId)
                .map(PortfolioResponseDTO::fromProto);
    }
}