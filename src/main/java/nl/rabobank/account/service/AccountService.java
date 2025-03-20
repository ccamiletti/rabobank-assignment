package nl.rabobank.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.exception.AccountException;
import nl.rabobank.account.exception.CardException;
import nl.rabobank.account.model.CardTypeEnum;
import nl.rabobank.account.model.CurrentBalanceResponse;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import nl.rabobank.account.repository.AccountRepository;
import nl.rabobank.account.repository.CardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final CardServiceLocator cardServiceLocator;

    public Flux<CurrentBalanceResponse> getBalance() {
        return accountRepository.findAll()
                .flatMap(account -> cardRepository.findByIban(account.getIban())
                        .map(card -> CurrentBalanceResponse.builder()
                                .accountNumber(account.getIban())
                                .balance(account.getBalance())
                                .cardNumber(card.getNumber())
                                .build()
                        )
                );
    }

    @Transactional
    public Mono<Void> withdraw(final WithdrawalRequest withdrawalRequest) {
        return getCardServiceByCardNumber(withdrawalRequest.getCardNumber())
                .flatMap(cardService -> cardService.withdraw(withdrawalRequest))
                .then();
    }

    @Transactional
    public Mono<Void> transfer(final TransferRequest transferRequest) {
        return getCardServiceByAccountNumber(transferRequest.getOriginAccountNumber())
                .flatMap(cardService -> cardService.transfer(transferRequest))
                .then();
    }

    private Mono<CardService> getCardServiceByCardNumber(Long cardNumber) {
        return cardRepository.findByNumber(cardNumber)
                .map(cardEntity -> findCardServiceByCardType(cardEntity.getType()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Card with number {} does not exist", cardNumber);
                    return Mono.error(new AccountException("Card NOT FOUND", HttpStatus.INTERNAL_SERVER_ERROR));
                }));

    }

    private Mono<CardService> getCardServiceByAccountNumber(String iban) {
        return cardRepository.findByIban(iban)
                .map(cardEntity -> findCardServiceByCardType(cardEntity.getType()))
                .switchIfEmpty(getMonoErrorCardNotFound(iban));
    }

    private CardService findCardServiceByCardType(CardTypeEnum cardTypeEnum) {
        CardService cardService = cardServiceLocator.getService(cardTypeEnum);
        if (cardService == null) {
            throw new CardException("Error getting service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return cardService;
    }

    private Mono<CardService> getMonoErrorCardNotFound(final String iban) {
        return Mono.defer(() -> {
            log.error("Account with IBAN {} does not have any cards linked", iban);
            return Mono.error(new AccountException("Card NOT FOUND", HttpStatus.INTERNAL_SERVER_ERROR));
        });
    }

}
