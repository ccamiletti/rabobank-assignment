package nl.rabobank.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.entity.AccountEntity;
import nl.rabobank.account.entity.AccountTransactionEntity;
import nl.rabobank.account.exception.AccountException;
import nl.rabobank.account.model.CardTypeEnum;
import nl.rabobank.account.model.TransactionTypeEnum;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.repository.AccountRepository;
import nl.rabobank.account.repository.TransactionRepository;
import nl.rabobank.account.util.AccountBalance;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardServiceCommon {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    protected final AccountBalance subtractBalance = (a, b ) -> (a - b);
    protected final AccountBalance addBalance = Double::sum;

    protected Mono<AccountEntity> validateTransfer(final String targetAccountNumber, final String originAccountNumber, Double amount) {
        return findAccountByIban(targetAccountNumber)
                .flatMap(targetAccount -> findAccountByIban(originAccountNumber))
                .filter(originAccount -> isEnoughBalance(originAccount, amount));
    }

    protected Mono<AccountEntity> updateAccountBalance(final AccountEntity accountEntity) {
        return accountRepository.save(accountEntity);
    }

    protected Mono<AccountTransactionEntity> saveOperation(final String iban,
                                                           final Double amount,
                                                           final TransactionTypeEnum transactionType,
                                                           final Double percentage,
                                                           final CardTypeEnum cardType) {

        log.debug("Saving Withdrawal. Account: {}, amount: {}", iban, amount);
        AccountTransactionEntity accountTransactionEntity =
                getAccountTransactionEntity(iban, null, amount, percentage, transactionType, cardType);
        return saveTransaction(accountTransactionEntity);
    }

    protected Mono<AccountTransactionEntity> saveOperation(final TransferRequest transferRequest,
                                                           final TransactionTypeEnum transactionType,
                                                           final Double percentage,
                                                           final CardTypeEnum cardType) {
        log.debug("Saving transfer. Origin account: {} target account: {}, amount: {}",
                transferRequest.getOriginAccountNumber(), transferRequest.getTargetAccountNumber(), transferRequest.getAmount());

        AccountTransactionEntity accountTransactionEntity =
                getAccountTransactionEntity(transferRequest.getOriginAccountNumber(),
                        transferRequest.getTargetAccountNumber(),
                        transferRequest.getAmount(),
                        percentage,
                        transactionType,
                        cardType);
        return saveTransaction(accountTransactionEntity);
    }

    private Mono<AccountTransactionEntity> saveTransaction(final AccountTransactionEntity accountTransactionEntity) {
        return transactionRepository.save(accountTransactionEntity);
    }

    protected Boolean isEnoughBalance(final AccountEntity accountEntity, final Double withdrawalAmount) {
        if (accountEntity.getBalance().compareTo(withdrawalAmount) >= 0) {
            return Boolean.TRUE;
        } else {
            log.error("Not Enough Balance in account {}", accountEntity.getIban());
            throw new AccountException("NOT ENOUGH BALANCE", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected Mono<AccountEntity> findAccountByIban(final String iban) {
        return accountRepository.findByIban(iban)
                .switchIfEmpty(getMonoErrorAccountNotFound(iban));
    }

    private Mono<AccountEntity> getMonoErrorAccountNotFound(final String iban) {
        return Mono.defer(() -> {
            log.error("Account with IBAN {} not found", iban);
            return Mono.error(new AccountException("Account NOT FOUND", HttpStatus.INTERNAL_SERVER_ERROR));
        });
    }

    private AccountTransactionEntity getAccountTransactionEntity(final String originAccountNumber,
                                                                 final String targetAccountNumber,
                                                                 final Double amount,
                                                                 final Double percentage,
                                                                 final TransactionTypeEnum transactionType,
                                                                 final CardTypeEnum cardType) {
        return AccountTransactionEntity.builder()
                .originAccountNumber(originAccountNumber)
                .targetAccountNumber(targetAccountNumber)
                .amount(amount)
                .percentage(percentage)
                .date(LocalDateTime.now())
                .type(transactionType)
                .cardType(cardType)
                .build();
    }

}
