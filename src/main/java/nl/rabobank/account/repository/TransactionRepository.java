package nl.rabobank.account.repository;

import nl.rabobank.account.entity.AccountTransactionEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface TransactionRepository extends R2dbcRepository<AccountTransactionEntity, Long> {

}
