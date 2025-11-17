package com.sood.portfolio.model;

import com.example.market.grpc.PortfolioResponse;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Serdeable
public class PortfolioResponseDTO {

    private final Long id;
    private final String userId;
    private final String portfolioName;
    private final BigDecimal totalInvested;
    private final BigDecimal totalCurrentValue;
    private final BigDecimal totalProfit;
    private final String totalProfitPercentage;
    private final List<PortfolioItemDTO> items;

    public static PortfolioResponseDTO fromProto(final PortfolioResponse proto) {
        final List<PortfolioItemDTO> items = proto.getItemsList().stream()
                .map(PortfolioItemDTO::fromProto)
                .toList();

        return PortfolioResponseDTO.builder()
                .id(proto.getPortfolioId())
                .userId(proto.getUserId())
                .portfolioName(proto.getPortfolioName())
                .totalInvested(new BigDecimal(proto.getInvestedValue()))
                .totalCurrentValue(new BigDecimal(proto.getCurrentValue()))
                .totalProfit(new BigDecimal(proto.getProfitValue()))
                .totalProfitPercentage(proto.getProfitPercentage())
                .items(items)
                .build();
    }
}
