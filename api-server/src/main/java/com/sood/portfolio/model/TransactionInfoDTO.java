package com.sood.portfolio.model;

import com.example.market.grpc.TransactionInfo;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Serdeable
public class TransactionInfoDTO {

    private final String date;
    private final double purchasePrice;
    private final double actualPrice;
    private final double quantity;

    public static TransactionInfoDTO fromProto(final TransactionInfo transactionInfo) {
        return TransactionInfoDTO.builder()
                .date(transactionInfo.getDate())
                .purchasePrice(transactionInfo.getPurchasePrice())
                .actualPrice(transactionInfo.getActualPrice())
                .quantity(transactionInfo.getQuantity())
                .build();
    }
}
