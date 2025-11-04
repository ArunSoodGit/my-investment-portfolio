package sood.found;

import io.micronaut.serde.annotation.Serdeable;
import java.time.LocalDate;

@Serdeable
public record TransactionCreatedEvent(
        String userId,
        String foundName,
        String symbol,
        double quantity,
        double price,
        String type,
        LocalDate date
) {
}