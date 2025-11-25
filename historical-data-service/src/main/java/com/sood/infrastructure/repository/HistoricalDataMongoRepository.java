package com.sood.infrastructure.repository;

import com.sood.infrastructure.entity.HistoricalDataEntity;
import io.micronaut.data.mongodb.annotation.MongoRepository;
import io.micronaut.data.repository.CrudRepository;
import java.util.List;
import org.bson.types.ObjectId;

@MongoRepository
public interface HistoricalDataMongoRepository extends CrudRepository<HistoricalDataEntity, ObjectId> {

    List<HistoricalDataEntity> findByPortfolioId(Long portfolioId);

    HistoricalDataEntity save(HistoricalDataEntity entity);

}
