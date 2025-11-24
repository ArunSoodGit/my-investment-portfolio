package com.sood.transaction.application;

import com.example.market.grpc.TransactionAddRequest;
import com.sood.transaction.domain.model.Transaction;
import com.sood.transaction.infrastructure.entity.TransactionEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

@Singleton
public class TransactionMapper {

    public TransactionCreatedEvent mapToEvent(final Transaction transaction) {
        return new TransactionCreatedEvent(
                transaction.getPortfolioId(),
                transaction.getSymbol(),
                transaction.getQuantity(),
                BigDecimal.valueOf(transaction.getPrice()),
                transaction.getType(),
                transaction.getDate()
        );
    }

    public Transaction toDomain(final TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getPortfolioId(),
                entity.getSymbol(),
                entity.getQuantity(),
                entity.getPrice(),
                entity.getDate(),
                entity.getType()
        );
    }

    public Transaction fromAddRequest(final TransactionAddRequest request, final TransactionType transactionType) {
        return new Transaction(
                null,
                request.getPortfolioId(),
                request.getSymbol(),
                request.getQuantity(),
                request.getPrice(),
                LocalDate.parse(request.getDate()),
                transactionType
        );
    }

    public TransactionEntity toEntity(final Transaction transaction) {
        final TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.getId());
        entity.setPortfolioId(transaction.getPortfolioId());
        entity.setSymbol(transaction.getSymbol());
        entity.setQuantity(transaction.getQuantity());
        entity.setPrice(transaction.getPrice());
        entity.setDate(transaction.getDate());
        entity.setType(transaction.getType());
        return entity;
    }
}