package com.sood.transaction.grpc;

import com.sood.transaction.TransactionType;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class TransactionGrpcRequest {

    final String symbol;
    final double quantity;
    final double purchasePrice;
    final TransactionType transactionType;
    final String date;
}
