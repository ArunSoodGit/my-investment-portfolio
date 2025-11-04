package com.sood.portfolio.model;

import com.example.market.grpc.PortfolioItem;
import com.example.market.grpc.TransactionInfo;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Serdeable
public class PortfolioItemDTO {

    final String symbol;
    final String name;
    final String type;
    final double quantity;
    final double purchasePrice;
    final double currentPrice;
    final String currency;
    final String purchaseDate;
    final double totalValue;
    final double profitLoss;
    final List<TransactionInfoDTO> transactionInfos;

    public static PortfolioItemDTO fromProto(final PortfolioItem proto) {
        final List<TransactionInfoDTO> transactionInfos = proto.getBuyTransactionsList().stream()
                .map(TransactionInfoDTO::fromProto)
                .toList();

        return PortfolioItemDTO.builder()
                .symbol(proto.getSymbol())
                .name(proto.getName())
                .quantity(proto.getQuantity())
                .purchasePrice(proto.getPurchasePrice())
                .currentPrice(proto.getCurrentPrice())
                .currency(proto.getCurrency())
                .purchaseDate(proto.getPurchaseDate())
                .totalValue(proto.getTotalValue())
                .profitLoss(proto.getProfit())
                .transactionInfos(transactionInfos)
                .build();
    }
}
