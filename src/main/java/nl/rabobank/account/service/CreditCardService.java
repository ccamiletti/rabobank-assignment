package nl.rabobank.account.service;

import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.entity.AccountEntity;
import nl.rabobank.account.entity.CardEntity;
import nl.rabobank.account.exception.CardException;
import nl.rabobank.account.model.CardTypeEnum;
import nl.rabobank.account.model.TransactionTypeEnum;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import nl.rabobank.account.repository.AccountRepository;
import nl.rabobank.account.repository.CardRepository;
import nl.rabobank.account.repository.TransactionRepository;
import nl.rabobank.account.util.AccountBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CreditCardService extends CardServiceCommon implements CardService {

    public final CardRepository cardRepository;

    @Value("${app.credit-card-amount-percentage}")
    private Double creditCardAmountPercent;

    public CreditCardService(AccountRepository accountRepository, TransactionRepository transactionRepository, CardRepository cardRepository) {
        super(accountRepository, transactionRepository);
        this.cardRepository = cardRepository;
    }

    @Override
    @Transactional
    public Mono<Void> withdraw(WithdrawalRequest withdrawalRequest) {
        return cardRepository.findByNumber(withdrawalRequest.getCardNumber())
                .switchIfEmpty(getMonoErrorCardNotFound(withdrawalRequest.getCardNumber()))
                .flatMap(cardEntity -> findAccountByIban(cardEntity.getIban())
                        .filter(accountEntity -> isEnoughBalance(accountEntity, applyCharge(withdrawalRequest.getAmount())))
                        .flatMap(accountEntity -> updateAccountBalance(accountEntity, applyCharge(withdrawalRequest.getAmount()), subtractBalance))
                        .flatMap(accountEntity -> saveOperation(accountEntity.getIban(), withdrawalRequest.getAmount(),
                                TransactionTypeEnum.WITHDRAWAL, getPercentage(withdrawalRequest.getAmount()),
                                CardTypeEnum.CREDIT_CARD)))
                .then();
    }

    private Mono<AccountEntity> updateAccountBalance(final AccountEntity accountEntity, final Double amount, final AccountBalance accountBalance) {
        Double newBalance = accountBalance.apply(accountEntity.getBalance(), amount);
        return updateAccountBalance(accountEntity.toBuilder().balance(newBalance).build());
    }

    private Double applyCharge(final Double amount) {
        Double per = getPercentage(amount);
        return (amount + per);
    }

    private Double getPercentage(final Double amount) {
        return  (amount * creditCardAmountPercent) / 100;
    }

    @Override
    @Transactional
    public Mono<Void> transfer(TransferRequest transferRequest) {
        return validateTransfer(transferRequest.getTargetAccountNumber(), transferRequest.getOriginAccountNumber(), applyCharge(transferRequest.getAmount()))
                .flatMap(originAccount -> updateAccountBalance(originAccount, applyCharge(transferRequest.getAmount()), subtractBalance))
                .flatMap(originAccountEntity -> findAccountByIban(transferRequest.getTargetAccountNumber()))
                .flatMap(targetAccount -> updateAccountBalance(targetAccount, transferRequest.getAmount(), addBalance))
                .flatMap(targetAccount ->
                        saveOperation(transferRequest,
                                TransactionTypeEnum.TRANSFER, getPercentage(transferRequest.getAmount()),
                                CardTypeEnum.CREDIT_CARD))
                .then();

    }

    private Mono<CardEntity> getMonoErrorCardNotFound(final Long cardNumber) {
        return Mono.defer(() -> {
            log.error("Card with number {} not found", cardNumber);
            return Mono.error(new CardException("Card NOT FOUND", HttpStatus.INTERNAL_SERVER_ERROR));
        });
    }

}
