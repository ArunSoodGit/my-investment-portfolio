package com.sood.transaction.infrastructure.service;

import com.sood.transaction.infrastructure.entity.TransactionEntity;
import com.sood.transaction.infrastructure.repository.TransactionRepository;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class TransactionService {

    private static final String TRANSACTION_NOT_FOUND_MESSAGE = "Transaction with ID %d not found";

    private final TransactionRepository repository;

    public TransactionService(final TransactionRepository repository) {
        this.repository = repository;
    }

    public TransactionEntity findByTransactionId(final Long transactionId) {
        return repository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format(TRANSACTION_NOT_FOUND_MESSAGE, transactionId)
                ));
    }

    public List<TransactionEntity> findByPortfolioIdAndSymbol(long portfolioId, final String symbol) {
        return repository.findByPortfolioIdAndSymbol(portfolioId, symbol);
    }
}
