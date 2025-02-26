package nl.rabobank.account.repository;

import nl.rabobank.account.entity.AccountEntity;
import nl.rabobank.account.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends R2dbcRepository<AccountEntity, Long> {

    Mono<AccountEntity> findByIban(String iban);

}
