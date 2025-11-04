package com.sood.transaction.infrastructure.event;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import sood.found.TransactionCreatedEvent;

@KafkaClient
public interface TransactionEventPublisher {

    @Topic("transaction-created")
    void publish(TransactionCreatedEvent event);
}
