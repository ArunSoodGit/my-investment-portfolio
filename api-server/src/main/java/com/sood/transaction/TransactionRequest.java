package com.sood.transaction;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class TransactionRequest {

    final String symbol;
    final double quantity;
    final double purchasePrice;
    final TransactionType transactionType;
    final String date;
}
