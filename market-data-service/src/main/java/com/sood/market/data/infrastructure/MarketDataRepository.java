package com.sood.market.data.infrastructure;

import com.sood.market.data.infrastructure.entity.MarketDataEntity;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.jpa.repository.JpaRepository;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketDataEntity, Long> {
}
