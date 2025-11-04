package com.sood.infrastructure.repository;

import com.sood.infrastructure.entity.PortfolioEntity;
import io.lettuce.core.dynamic.annotation.Param;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioEntity, Long> {

    @Query("""
                SELECT DISTINCT p FROM PortfolioEntity p
                LEFT JOIN FETCH p.items i
                WHERE p.id = :portfolioId
            """)
    Optional<PortfolioEntity> findByIdWithItemsAndTransactions(
            @Param("id") Long portfolioId
    );

    @Query("""
                SELECT DISTINCT p FROM PortfolioEntity p
                LEFT JOIN FETCH p.items i
            """)
    List<PortfolioEntity> findAllWithItemsAndTransactions();
}
