package com.sood.transaction.application.component;

import com.sood.transaction.domain.model.Transaction;
import com.sood.transaction.domain.port.TransactionPersistencePort;
import io.micronaut.core.annotation.Order;
import jakarta.inject.Singleton;
import sood.found.TransactionType;

@Singleton
@Order(2)
public class UpdateSellTransactionOperationComponent implements TransactionOperationComponent {

    private final TransactionPersistencePort persistencePort;

    public UpdateSellTransactionOperationComponent(final TransactionPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public boolean supports(final TransactionType type) {
        return type == TransactionType.SELL;
    }

    @Override
    public void execute(final Transaction transaction) {
        transaction.setType(TransactionType.SELL);
        persistencePort.update(transaction);
    }
}