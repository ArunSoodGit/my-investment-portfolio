package com.sood.infrastructure.adapter;

import com.sood.application.HistoricalDataPort;
import com.sood.application.HistoricalDataSaveCommand;
import com.sood.application.command.HistoricalDataCommand;
import com.sood.application.result.HistoricalData;
import com.sood.application.result.HistoricalDataResult;
import com.sood.infrastructure.entity.HistoricalDataEntity;
import com.sood.infrastructure.repository.HistoricalDataMongoRepository;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class HistoricalDataAdapter implements HistoricalDataPort {

    private final HistoricalDataMongoRepository mongoRepository;

    public HistoricalDataAdapter(final HistoricalDataMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public HistoricalDataResult getHistoricalData(final HistoricalDataCommand command) {
        final List<HistoricalDataEntity> entities = mongoRepository.findByPortfolioId(command.portfolioId());
        return new HistoricalDataResult(command.portfolioId(), entities.stream()
                .map(historicalDataEntity ->
                        new HistoricalData(String.valueOf(historicalDataEntity.getDate()),
                                historicalDataEntity.getInvestedValue(), historicalDataEntity.getCurrentValue()))
                .toList());
    }

    @Override
    public void save(final HistoricalDataSaveCommand command) {
        mongoRepository.save(HistoricalDataEntity.from(command));
    }
}
