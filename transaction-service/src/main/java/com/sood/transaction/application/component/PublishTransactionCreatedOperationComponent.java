package com.sood.transaction.application.component;

import com.sood.transaction.application.TransactionMapper;
import com.sood.transaction.domain.model.Transaction;
import com.sood.transaction.infrastructure.event.TransactionEventPublisher;
import io.micronaut.core.annotation.Order;
import jakarta.inject.Singleton;
import sood.found.TransactionCreatedEvent;
import sood.found.TransactionType;

@Singleton
@Order(2)
public class PublishTransactionCreatedOperationComponent implements TransactionOperationComponent {

    private final TransactionEventPublisher eventPublisher;
    private final TransactionMapper mapper;

    public PublishTransactionCreatedOperationComponent(final TransactionEventPublisher eventPublisher,
            final TransactionMapper mapper) {
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    @Override
    public boolean supports(final TransactionType type) {
        return true;
    }

    @Override
    public void execute(final Transaction transaction) {
        final TransactionCreatedEvent event = mapper.mapToEvent(transaction);
        eventPublisher.publish(event);
    }
}