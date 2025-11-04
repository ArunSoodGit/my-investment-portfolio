package com.sood.transaction.application;

import com.example.market.grpc.TransactionResponse;
import com.sood.transaction.application.component.TransactionOperationComponent;
import com.sood.transaction.infrastructure.entity.TransactionEntity;
import jakarta.inject.Singleton;
import java.util.List;
import sood.found.TransactionType;

@Singleton
public class TransactionProcessor {

    private final List<TransactionOperationComponent> operations;

    public TransactionProcessor(final List<TransactionOperationComponent> operations) {
        this.operations = operations;
    }

    public TransactionResponse process(final TransactionEntity entity, final TransactionType transactionType) {
        operations.stream()
                .filter(operation -> operation.supports(transactionType))
                .forEach(operation -> operation.execute(entity));

        return TransactionResponse.newBuilder()
                .setStatus("OK")
                .build();
    }
}