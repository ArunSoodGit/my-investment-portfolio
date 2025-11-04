package com.sood.portfolio.history;

import com.example.market.grpc.PortfolioHistoryItem;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@Serdeable
public class PortfolioHistoryDTO {

    private final String date;
    private final BigDecimal totalInvested;
    private final BigDecimal totalCurrentValue;

    public static PortfolioHistoryDTO fromProto(final PortfolioHistoryItem proto) {
        return PortfolioHistoryDTO.builder()
                .date(proto.getDate())
                .totalCurrentValue(new BigDecimal(proto.getCurrentValue()))
                .totalInvested(new BigDecimal(proto.getInvestedValue()))
                .build();
    }
}
