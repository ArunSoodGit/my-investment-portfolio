package com.sood.transaction.model;

import com.example.market.grpc.Transaction;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Serdeable
public class TransactionDTO {

    private final Long id;
    private final String date;
    private final String symbol;
    private final BigDecimal purchasePrice;
    private final BigDecimal currentPrice;
    private final BigDecimal quantity;
    private final String profitPercentage;

    public static TransactionDTO fromProto(final Transaction proto) {
        return TransactionDTO.builder()
                .id(proto.getId())
                .symbol(proto.getSymbol())
                .date(proto.getDate())
                .currentPrice(new BigDecimal(proto.getCurrentPrice()))
                .purchasePrice(new BigDecimal(proto.getPrice()))
                .quantity(new BigDecimal(proto.getQuantity()))
                .profitPercentage(proto.getProfitPercentage())
                .build();
    }
}