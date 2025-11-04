package com.sood.transaction.application.component;

import com.sood.transaction.infrastructure.entity.TransactionEntity;
import sood.found.TransactionType;

public interface TransactionOperationComponent {

    boolean supports(TransactionType type);

    void execute(TransactionEntity entity);
}
