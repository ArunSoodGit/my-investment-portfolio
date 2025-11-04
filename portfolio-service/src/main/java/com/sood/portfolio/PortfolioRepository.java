package com.sood.portfolio;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface PortfolioRepository extends CrudRepository<PortfolioEntity, Long> {

    PortfolioEntity findByUserIdAndFoundName(String userId, String foundName);
}
