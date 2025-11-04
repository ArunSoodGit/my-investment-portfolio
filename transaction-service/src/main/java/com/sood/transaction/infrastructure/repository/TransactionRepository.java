package com.sood.transaction.infrastructure.repository;

import com.sood.transaction.infrastructure.entity.TransactionEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findByPortfolioIdAndSymbol(long portfolioId, String symbol);
}
