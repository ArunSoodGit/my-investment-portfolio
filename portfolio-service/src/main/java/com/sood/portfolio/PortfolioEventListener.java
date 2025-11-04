package com.sood.portfolio;

import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import sood.found.TransactionCreatedEvent;

@KafkaListener(groupId = "portfolio-service")
public class PortfolioEventListener {

    private final PortfolioService portfolioService;

    public PortfolioEventListener(final PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Topic("transaction-created")
    public void receive(final TransactionCreatedEvent event) {
        portfolioService.updatePortfolio(event);
    }
}