package com.sood.portfolio.model;

import com.example.market.grpc.PortfolioItem;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Serdeable
public class PortfolioItemDTO {

    private final String symbol;
    private final String name;
    private final String exchange;
    private final BigDecimal currentPrice;
    private final double totalQuantity;
    private final BigDecimal totalValue;
    private final BigDecimal averagePurchasePrice;
    private final String percentageChange;
    private final String currency;
    private final BigDecimal profit;
    private final String profitPercentage;

    public static PortfolioItemDTO fromProto(final PortfolioItem proto) {
        return PortfolioItemDTO.builder()
                .symbol(proto.getSymbol())
                .name(proto.getName())
                .exchange(proto.getExchange())
                .totalQuantity(proto.getQuantity())
                .averagePurchasePrice(new BigDecimal(proto.getAveragePurchasePrice()))
                .percentageChange(proto.getPercentageChange())
                .currentPrice(new BigDecimal(proto.getCurrentPrice()))
                .currency(proto.getCurrency())
                .totalValue(new BigDecimal(proto.getTotalValue()))
                .profit(new BigDecimal(proto.getProfit()))
                .profitPercentage(proto.getProfitPercentage())
                .build();
    }
}
