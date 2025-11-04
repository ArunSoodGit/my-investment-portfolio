package com.sood;

import com.sood.portfolio.PortfolioGrpcClient;
import com.sood.portfolio.model.PortfolioDTO;
import com.sood.transaction.TransactionDTO;
import com.sood.transaction.TransactionGrpcClient;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import market.Transaction;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Controller("/v1/api")
public class ApiGatewayController {

    private final PortfolioGrpcClient portfolioClient;
    private final TransactionGrpcClient transactionClient;

    @Inject
    public ApiGatewayController(final PortfolioGrpcClient portfolioClient, final TransactionGrpcClient transactionClient) {
        this.portfolioClient = portfolioClient;
        this.transactionClient = transactionClient;
    }

    @Get("/portfolio/{userId}/{foundName}")
    public Mono<PortfolioDTO> getPortfolio(@PathVariable final String userId, @PathVariable final String foundName) {
        return Mono.fromCallable(() -> portfolioClient.getPortfolio(userId, foundName)).map(PortfolioDTO::fromProto);
    }

    @Post("/portfolio/{userId}/{foundName}/transaction")
    public Mono<Boolean> addTransaction(@PathVariable String userId, @PathVariable final String foundName,
            @Body TransactionDTO transactionDTO) {
        return Mono.fromCallable(() -> {
            final Transaction.TransactionResponse response = transactionClient.addTransaction(userId, foundName, transactionDTO);
            return "OK".equals(response.getStatus());
        }).subscribeOn(Schedulers.boundedElastic());
    }
}