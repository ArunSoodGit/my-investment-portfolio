package com.sood.portfolio.model;

import com.example.market.grpc.PortfolioResponse;
import io.micronaut.serde.annotation.Serdeable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Serdeable
public class PortfolioDTO {

    private final String userId;
    private final Double totalInvested;
    private final Double totalCurrentValue;
    private final Double totalProfitLoss;
    private final List<PortfolioItemDTO> items;

    public static PortfolioDTO fromProto(final PortfolioResponse proto) {
        final List<PortfolioItemDTO> items = proto.getItemsList().stream()
                .map(PortfolioItemDTO::fromProto)
                .toList();

        return PortfolioDTO.builder()
                .userId(proto.getUserId())
                .totalInvested(proto.getTotalInvestedValue())
                .totalCurrentValue(proto.getTotalCurrentValue())
                .totalProfitLoss(proto.getTotalProfitValue())
                .items(items)
                .build();
    }
}
