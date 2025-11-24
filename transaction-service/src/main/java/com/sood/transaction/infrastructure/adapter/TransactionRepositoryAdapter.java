package com.sood.transaction.infrastructure.adapter;

import com.sood.transaction.application.TransactionMapper;
import com.sood.transaction.domain.model.Transaction;
import com.sood.transaction.domain.port.TransactionPersistencePort;
import com.sood.transaction.infrastructure.repository.TransactionRepository;
import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class TransactionRepositoryAdapter implements TransactionPersistencePort {

    private final TransactionRepository jpaRepository;
    private final TransactionMapper mapper;

    public TransactionRepositoryAdapter(final TransactionRepository jpaRepository, final TransactionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(final Transaction transaction) {
        jpaRepository.save(mapper.toEntity(transaction));
    }

    @Override
    public Transaction findById(final Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow();
    }

    @Override
    public List<Transaction> findAllByPortfolioIdAndSymbol(final Long portfolioId, final String symbol) {
        return jpaRepository.findAllByPortfolioIdAndSymbol(portfolioId, symbol).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void update(Transaction transaction) {
        jpaRepository.update(mapper.toEntity(transaction));
    }
}