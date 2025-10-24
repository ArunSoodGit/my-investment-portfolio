package com.sood.transaction;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class TransactionDTO {

    final String symbol;
    final double quantity;
    final double price;
    final String type; // "BUY" / "SELL"
    final String date;
}
