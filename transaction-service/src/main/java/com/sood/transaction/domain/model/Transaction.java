package com.sood.transaction.domain.model;

import java.time.LocalDate;
import sood.found.TransactionType;

public class Transaction {

    private Long id;
    private Long portfolioId;
    private String symbol;
    private double quantity;
    private double price;
    private LocalDate date;
    private TransactionType type;

    public Transaction(final Long id, final Long portfolioId, final String symbol,
            final double quantity, final double price, final LocalDate date, final TransactionType type) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}