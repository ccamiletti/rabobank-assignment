package nl.rabobank.account.service;

import nl.rabobank.account.entity.AccountEntity;
import nl.rabobank.account.model.CardTypeEnum;
import nl.rabobank.account.model.TransactionTypeEnum;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import nl.rabobank.account.repository.AccountRepository;
import nl.rabobank.account.repository.CardRepository;
import nl.rabobank.account.repository.TransactionRepository;
import nl.rabobank.account.util.AccountBalance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DebitCardService extends CardServiceCommon implements CardService {

    private final CardRepository cardRepository;

    @Value("${app.debit-card-amount-percentage}")
    private Double debitCardAmountPercent;

    public DebitCardService(AccountRepository accountRepository, TransactionRepository transactionRepository, CardRepository cardRepository) {
        super(accountRepository, transactionRepository);
        this.cardRepository = cardRepository;
    }

    @Override
    public Mono<Void> withdraw(final WithdrawalRequest withdrawalRequest) {
        return cardRepository.findByNumber(withdrawalRequest.getCardNumber())
                .flatMap(cardEntity -> findAccountByIban(cardEntity.getIban())
                        .filter(accountEntity -> isEnoughBalance(accountEntity, withdrawalRequest.getAmount()))
                        .flatMap(accountEntity -> updateAccountBalance(accountEntity, withdrawalRequest.getAmount(), subtractBalance))
                        .flatMap(accountEntity ->
                                saveOperation(accountEntity.getIban(), withdrawalRequest.getAmount(),
                                        TransactionTypeEnum.WITHDRAWAL, debitCardAmountPercent, CardTypeEnum.DEBIT_CARD)))
                .then();

    }

    @Override
    public Mono<Void> transfer(final TransferRequest transferRequest) {
        return validateTransfer(transferRequest.getTargetAccountNumber(), transferRequest.getOriginAccountNumber(), transferRequest.getAmount())
                .flatMap(accountEntity -> updateAccountBalance(accountEntity, transferRequest.getAmount(), subtractBalance))
                .flatMap(originAccountEntity -> findAccountByIban(transferRequest.getTargetAccountNumber()))
                .flatMap(targetAccount -> updateAccountBalance(targetAccount, transferRequest.getAmount(), addBalance))
                .flatMap(targetAccount ->
                        saveOperation(transferRequest, TransactionTypeEnum.TRANSFER, debitCardAmountPercent, CardTypeEnum.DEBIT_CARD))
                .then();
    }

    private Mono<AccountEntity> updateAccountBalance(final AccountEntity accountEntity, final Double amount, final AccountBalance accountBalance) {
        Double newBalance = accountBalance.apply(accountEntity.getBalance(), amount);
        return updateAccountBalance(accountEntity.toBuilder().balance(newBalance).build());
    }

    @Override
    public Mono<Void> payment(TransferRequest transferRequest) {
        return null;
    }

    @Override
    public Boolean isApplicable(CardTypeEnum cardTypeEnum) {
        return cardTypeEnum == CardTypeEnum.DEBIT_CARD;
    }

}
