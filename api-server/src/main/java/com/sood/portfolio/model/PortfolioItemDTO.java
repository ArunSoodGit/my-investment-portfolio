package com.sood.portfolio.model;

import com.example.market.grpc.PortfolioItem;
import io.micronaut.serde.annotation.Serdeable;
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

    public static PortfolioItemDTO fromProto(final PortfolioItem proto) {
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
                .build();
    }
}
