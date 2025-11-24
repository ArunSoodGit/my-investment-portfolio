package com.sood.transaction.application.component;

import com.sood.transaction.domain.model.Transaction;
import sood.found.TransactionType;

public interface TransactionOperationComponent {
    boolean supports(TransactionType type);

    void execute(Transaction transaction);
}
