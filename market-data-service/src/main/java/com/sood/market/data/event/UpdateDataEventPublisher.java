package com.sood.market.data.event;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;

@KafkaClient
public interface UpdateDataEventPublisher {

    @Topic("updated-data")
    void publish(final String msg);
}
