package com.sood.infrastructure.event;

import com.sood.application.portfolio.update.PortfolioUpdateHandler;
import io.micronaut.configuration.kafka.annotation.KafkaListener;
import io.micronaut.configuration.kafka.annotation.Topic;
import lombok.extern.log4j.Log4j2;
import sood.found.TransactionCreatedEvent;

/**
 * Kafka event listener for transaction events.
 * Receives transaction created events and triggers portfolio updates.
 */
@Log4j2
@KafkaListener(groupId = "portfolio-service")
public class PortfolioEventListener {

    private final PortfolioUpdateHandler portfolioUpdateHandler;

    public PortfolioEventListener(final PortfolioUpdateHandler portfolioUpdateHandler) {
        this.portfolioUpdateHandler = portfolioUpdateHandler;
    }

    /**
     * Processes a transaction created event from Kafka.
     * Updates the portfolio with the transaction information and handles any errors gracefully.
     *
     * @param event the transaction created event
     */
    @Topic("transaction-created")
    public void receive(final TransactionCreatedEvent event) {
        try {
            portfolioUpdateHandler.handle(event);
        } catch (Exception e) {
            log.error("Failed to handle transaction event: {}", event, e);
        }
    }

    @Topic("updated-data")
    public void receive(String msg) {
        try {
            portfolioUpdateHandler.handle();
        } catch (Exception e) {
            log.error("Failed to handle updated data event: ", e);
        }
    }
}