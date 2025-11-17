package com.sood.portfolio;

import com.sood.portfolio.model.PortfolioResponseDTO;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class PortfolioService {

    private final PortfolioGrpcClient portfolioClient;

    @Inject
    public PortfolioService(final PortfolioGrpcClient portfolioClient) {
        this.portfolioClient = portfolioClient;
    }

    public Flowable<PortfolioResponseDTO> streamPortfolio(final Long portfolioId) {
        return portfolioClient.streamPortfolio(portfolioId)
                .map(PortfolioResponseDTO::fromProto);
    }
}