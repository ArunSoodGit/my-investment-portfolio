package com.sood.infrastructure.repository;

import com.sood.infrastructure.entity.PortfolioEntity;
import com.sood.infrastructure.entity.PortfolioHistoryEntity;
import io.lettuce.core.dynamic.annotation.Param;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioHistoryRepository extends JpaRepository<PortfolioHistoryEntity, Long> {

    List<PortfolioHistoryEntity> findByPortfolio(PortfolioEntity portfolio);

    @Query("SELECT ph FROM PortfolioHistoryEntity ph WHERE ph.portfolio = :portfolio ORDER BY ph.date DESC")
    Optional<PortfolioHistoryEntity> findLatestByPortfolio(@Param("portfolio") PortfolioEntity portfolio);
}
