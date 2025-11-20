package com.sood.transaction;

import com.sood.transaction.grpc.TransactionGrpcRequest;
import com.sood.transaction.model.TransactionDTO;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.reactivex.rxjava3.core.Single;
import jakarta.inject.Inject;
import java.util.List;

@Controller("/v1/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Inject
    public TransactionController(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Get("/{portfolioId}/{symbol}")
    public Single<List<TransactionDTO>> getTransactions(@PathVariable final Long portfolioId,
            @PathVariable final String symbol) {
        return transactionService.getTransactions(portfolioId, symbol);
    }

    @Post("/{portfolioId}")
    public Single<Boolean> addTransaction(@PathVariable final Long portfolioId, @Body final TransactionGrpcRequest request) {
        return transactionService.addTransaction(portfolioId, request);
    }

    @Delete("/{transactionId}")
    public Single<Boolean> removeTransaction(@PathVariable final Long transactionId) {
        return transactionService.removeTransaction(transactionId);
    }
}