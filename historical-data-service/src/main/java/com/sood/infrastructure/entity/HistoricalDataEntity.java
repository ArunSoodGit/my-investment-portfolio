package com.sood.infrastructure.entity;

import com.sood.application.HistoricalDataSaveCommand;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.serde.annotation.Serdeable;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.bson.types.ObjectId;

@Serdeable
@MappedEntity("portfolios_history")
public class HistoricalDataEntity {

    @Id
    @GeneratedValue
    private ObjectId id;

    private Long portfolioId;
    private LocalDate date;
    private BigDecimal investedValue;
    private BigDecimal currentValue;

    public HistoricalDataEntity(final ObjectId id, final Long portfolioId, final LocalDate date,
            final BigDecimal investedValue, final BigDecimal currentValue) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.date = date;
        this.investedValue = investedValue;
        this.currentValue = currentValue;
    }

    public static HistoricalDataEntity from(final HistoricalDataSaveCommand command) {
        return new HistoricalDataEntity(null, command.portfolioId(), LocalDate.parse(command.date()),
                new BigDecimal(command.investedValue()), new BigDecimal(command.currentValue()));
    }


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getInvestedValue() {
        return investedValue;
    }

    public void setInvestedValue(BigDecimal investedValue) {
        this.investedValue = investedValue;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }
}
