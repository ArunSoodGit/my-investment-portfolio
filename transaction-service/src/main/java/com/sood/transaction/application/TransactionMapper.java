package com.sood.transaction.application;

import com.example.market.grpc.TransactionAddRequest;
import com.sood.transaction.infrastructure.entity.TransactionEntity;
import jakarta.inject.Singleton;
import java.math.BigDecimal;
import java.time.LocalDate;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

@Singleton
public class TransactionMapper {

    public TransactionEntity mapToEntity(final TransactionAddRequest request) {
        final TransactionEntity entity = new TransactionEntity();
        entity.setPortfolioId(request.getPortfolioId());
        entity.setSymbol(request.getSymbol());
        entity.setQuantity(request.getQuantity());
        entity.setPrice(request.getPrice());
        entity.setType(TransactionType.BUY);
        entity.setDate(LocalDate.parse(request.getDate()));
        return entity;
    }

    public TransactionCreatedEvent mapToEvent(final TransactionEntity entity) {
        return new TransactionCreatedEvent(
                entity.getPortfolioId(),
                entity.getSymbol(),
                entity.getQuantity(),
                BigDecimal.valueOf(entity.getPrice()),
                entity.getType(),
                entity.getDate()
        );
    }
}