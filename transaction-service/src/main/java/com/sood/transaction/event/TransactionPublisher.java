package com.sood.transaction.event;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import sood.found.TransactionCreatedEvent;

@KafkaClient
public interface TransactionPublisher {

    @Topic("transaction-created")
    void send(TransactionCreatedEvent event);
}
