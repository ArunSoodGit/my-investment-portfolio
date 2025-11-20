package com.sood.transaction;

import com.sood.transaction.grpc.TransactionGrpcRequest;
import jakarta.inject.Singleton;

@Singleton
public class TransactionValidator {
    public void validateGetTransactionsRequest(final Long portfolioId, final String symbol) {
        requirePositiveLong(portfolioId, "Portfolio ID must be a positive number");
        requireNonBlank(symbol, "Symbol cannot be null or blank");
    }

    public void validateAddTransactionRequest(final Long portfolioId, final TransactionGrpcRequest transactionGrpcRequest) {
        requirePositiveLong(portfolioId, "Portfolio ID must be a positive number");
        requireNonNull(transactionGrpcRequest, "Transaction request cannot be null");
        requireNonBlank(transactionGrpcRequest.getSymbol(), "Symbol cannot be null or blank");
        requirePositive(transactionGrpcRequest.getQuantity(), "Quantity must be greater than 0");
        requirePositive(transactionGrpcRequest.getPurchasePrice(), "Purchase price must be greater than 0");
        requireNonBlank(transactionGrpcRequest.getDate(), "Date cannot be null or blank");
        requireNonNull(transactionGrpcRequest.getTransactionType(), "Transaction type cannot be null");
    }

    public void validateRemoveTransactionRequest(final Long transactionId) {
        requirePositiveLong(transactionId, "Transaction ID must be a positive number");
    }

    private void requireNonNull(final Object value, final String message) {
        if (value == null) throw new IllegalArgumentException(message);
    }

    private void requireNonBlank(final String value, final String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
    }

    private void requirePositive(final double value, final String message) {
        if (value <= 0) throw new IllegalArgumentException(message);
    }

    private void requirePositiveLong(final Long value, final String message) {
        if (value == null || value <= 0) throw new IllegalArgumentException(message);
    }
}
