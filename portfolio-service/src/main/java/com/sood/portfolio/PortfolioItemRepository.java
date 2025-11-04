package com.sood.portfolio;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;

@Repository
public interface PortfolioItemRepository extends CrudRepository<PortfolioItemEntity, Long> {

    List<PortfolioItemEntity> findByUserId(String userId);
}