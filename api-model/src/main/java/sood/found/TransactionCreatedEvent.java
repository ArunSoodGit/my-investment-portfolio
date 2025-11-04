package sood.found;

import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Serdeable
public record TransactionCreatedEvent(
        Long portfolioId,
        String symbol,
        double quantity,
        BigDecimal price,
        TransactionType type,
        LocalDate date
) {
}