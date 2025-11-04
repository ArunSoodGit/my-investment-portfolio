package com.sood.transaction;

import com.sood.transaction.event.TransactionPublisher;
import io.micronaut.configuration.kafka.annotation.KafkaClient;
import jakarta.inject.Singleton;
import java.time.LocalDate;
import market.Transaction;
import sood.found.TransactionCreatedEvent;

@Singleton
public class TransactionService {

    private final TransactionRepository repository;

    @KafkaClient
    private TransactionPublisher publisher;

    public TransactionService(final TransactionRepository repository, final TransactionPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public Transaction.TransactionResponse addTransaction(final Transaction.TransactionRequest request) {
        final TransactionEntity entity = createEntity(request);

        repository.save(entity);

        // Publikacja eventu do Kafka
        final TransactionCreatedEvent event = createEvent(entity);
        publisher.send(event);

        return Transaction.TransactionResponse.newBuilder()
                .setStatus("OK")
                .build();
    }

    private TransactionEntity createEntity(final Transaction.TransactionRequest request) {
        final TransactionEntity entity = new TransactionEntity();
        entity.setUserId(request.getUserId());
        entity.setFoundName(request.getFoundName());
        entity.setSymbol(request.getSymbol());
        entity.setQuantity(request.getQuantity());
        entity.setPrice(request.getPrice());
        entity.setType(request.getType() == Transaction.TransactionType.BUY ? Transaction.TransactionType.BUY : Transaction.TransactionType.SELL);
        entity.setDate(LocalDate.parse(request.getDate()));
        return entity;
    }

    private TransactionCreatedEvent createEvent(final TransactionEntity entity) {
        return new TransactionCreatedEvent(
                entity.getUserId(),
                entity.getFoundName(),
                entity.getSymbol(),
                entity.getQuantity(),
                entity.getPrice(),
                entity.getType().name(),
                entity.getDate()
        );
    }
}