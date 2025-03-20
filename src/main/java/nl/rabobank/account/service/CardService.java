package nl.rabobank.account.service;

import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import reactor.core.publisher.Mono;

public interface CardService {

    Mono<Void> withdraw(final WithdrawalRequest withdrawalRequest);
    Mono<Void> transfer(final TransferRequest transferRequest);
}