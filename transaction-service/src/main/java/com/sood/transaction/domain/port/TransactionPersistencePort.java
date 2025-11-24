package com.sood.transaction.domain.port;

import com.sood.transaction.domain.model.Transaction;
import java.util.List;

public interface TransactionPersistencePort {
    void save(Transaction transaction);

    Transaction findById(Long id);

    List<Transaction> findAllByPortfolioIdAndSymbol(Long portfolioId, String symbol);

    void update(Transaction transaction);
}