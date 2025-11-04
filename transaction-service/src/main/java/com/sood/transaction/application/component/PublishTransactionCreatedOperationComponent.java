package com.sood.transaction.application.component;

import com.sood.transaction.application.TransactionMapper;
import com.sood.transaction.infrastructure.entity.TransactionEntity;
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
    public void execute(final TransactionEntity entity) {
        final TransactionCreatedEvent event = mapper.mapToEvent(entity);
        eventPublisher.publish(event);
    }
}