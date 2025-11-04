package com.sood.transaction.application.component;

import com.sood.transaction.infrastructure.entity.TransactionEntity;
import com.sood.transaction.infrastructure.repository.TransactionRepository;
import io.micronaut.core.annotation.Order;
import jakarta.inject.Singleton;
import sood.found.TransactionType;

@Singleton
@Order(2)
public class UpdateSellTransactionOperationComponent implements TransactionOperationComponent {

    private final TransactionRepository repository;

    public UpdateSellTransactionOperationComponent(final TransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean supports(final TransactionType type) {
        return type == TransactionType.SELL;
    }

    @Override
    public void execute(final TransactionEntity entity) {
        entity.setType(TransactionType.SELL);
        repository.update(entity);
    }
}