package com.sood.application;


import com.sood.application.command.HistoricalDataCommand;
import com.sood.application.result.HistoricalDataResult;

public interface HistoricalDataPort {

    HistoricalDataResult getHistoricalData(HistoricalDataCommand command);

    void save(HistoricalDataSaveCommand command);
}
