package nl.rabobank.account.repository;

import nl.rabobank.account.entity.CardEntity;
import nl.rabobank.account.entity.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CardRepository extends R2dbcRepository<CardEntity, Long> {

    Mono<CardEntity> findByNumber(Long cardNumber);
    Mono<CardEntity> findByIban(String iban);

}
