package com.sood.infrastructure.event;

import com.sood.application.portfolio.PortfolioUpdateHandler;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import sood.found.TransactionCreatedEvent;

@KafkaListener(groupId = "portfolio-service")
public class PortfolioEventListener {

    private final PortfolioUpdateHandler portfolioUpdateHandler;

    public PortfolioEventListener(final PortfolioUpdateHandler portfolioUpdateHandler) {
        this.portfolioUpdateHandler = portfolioUpdateHandler;
    }

    @Topic("transaction-created")
    public void receive(final TransactionCreatedEvent event) {
        portfolioUpdateHandler.handle(event);
    }
}