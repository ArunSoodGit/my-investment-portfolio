package com.sood.transaction.application.component;

import com.sood.transaction.infrastructure.entity.TransactionEntity;
import com.sood.transaction.infrastructure.repository.TransactionRepository;
import io.micronaut.core.annotation.Order;
import jakarta.inject.Singleton;
import sood.found.TransactionType;

@Singleton
@Order(1)
public class SaveBuyTransactionOperationComponent implements TransactionOperationComponent {

    private final TransactionRepository repository;

    public SaveBuyTransactionOperationComponent(final TransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(final TransactionType type) {
        return TransactionType.BUY == type;
    }

    @Override
    public void execute(final TransactionEntity entity) {
        repository.save(entity);
    }
}