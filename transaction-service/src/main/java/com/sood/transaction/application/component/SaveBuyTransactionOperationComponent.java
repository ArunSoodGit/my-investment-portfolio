package com.sood.transaction.application.component;

import com.sood.transaction.domain.model.Transaction;
import com.sood.transaction.domain.port.TransactionPersistencePort;
import io.micronaut.core.annotation.Order;
import jakarta.inject.Singleton;
import sood.found.TransactionType;

@Singleton
@Order(1)
public class SaveBuyTransactionOperationComponent implements TransactionOperationComponent {

    private final TransactionPersistencePort persistencePort;

    public SaveBuyTransactionOperationComponent(final TransactionPersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    @Override
    public boolean supports(final TransactionType type) {
        return TransactionType.BUY == type;
    }

    @Override
    public void execute(final Transaction transaction) {
        persistencePort.save(transaction);
    }
}