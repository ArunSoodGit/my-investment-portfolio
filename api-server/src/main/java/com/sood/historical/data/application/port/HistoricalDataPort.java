package com.sood.historical.data.application.port;

import com.sood.historical.data.application.command.HistoricalDataCommand;
import com.sood.historical.data.application.result.HistoricalDataResult;

public interface HistoricalDataPort {

    HistoricalDataResult getHistoricalData(HistoricalDataCommand portfolioId);
}
