package nl.rabobank.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.entity.AccountEntity;
import nl.rabobank.account.entity.CardEntity;
import nl.rabobank.account.exception.AccountException;
import nl.rabobank.account.model.CardTypeEnum;
import nl.rabobank.account.model.CurrentBalanceResponse;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import nl.rabobank.account.repository.AccountRepository;
import nl.rabobank.account.repository.CardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final List<CardService> cardServiceList;

    public Flux<CurrentBalanceResponse> getBalance() {
        return accountRepository.findAll()
                .map(account -> CurrentBalanceResponse.builder()
                        .accountNumber(account.getIban())
                        .balance(account.getBalance()).build());
    }


    public Mono<Void> withdraw(final WithdrawalRequest withdrawalRequest) {
        return getCardServiceByCardNumber(withdrawalRequest.getCardNumber())
                .flatMap(cardService -> cardService.withdraw(withdrawalRequest))
                .then();
    }

    public Mono<Void> transfer(final TransferRequest transferRequest) {
        return getCardServiceByAccountNumber(transferRequest.getOriginAccountNumber())
                .flatMap(cardService -> cardService.transfer(transferRequest))
                .then();
    }

    private Mono<CardService> getCardServiceByCardNumber(Long cardNumber) {
        Mono<CardEntity> cardEntityMono = cardRepository.findByNumber(cardNumber);
        return cardEntityMono.map(cardEntity -> findCardServiceByCardType(cardEntity.getType()));
    }

    private Mono<CardService> getCardServiceByAccountNumber(String iban) {
        return cardRepository.findByIban(iban)
                .map(cardEntity -> findCardServiceByCardType(cardEntity.getType()))
                .switchIfEmpty(getMonoErrorCardNotFound(iban));
    }

    private CardService findCardServiceByCardType(CardTypeEnum cardTypeEnum) {
        return cardServiceList.stream()
                .filter(cardService -> cardService.isApplicable(cardTypeEnum))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CardService not found"));
    }

    private Mono<CardService> getMonoErrorCardNotFound(final String iban) {
        return Mono.defer(() -> {
            log.error("Account with IBAN {} does not have any cards linked", iban);
            return Mono.error(new AccountException("Card NOT FOUND", HttpStatus.INTERNAL_SERVER_ERROR));
        });
    }

}
