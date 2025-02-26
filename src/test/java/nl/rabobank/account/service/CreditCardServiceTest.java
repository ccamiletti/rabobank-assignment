package nl.rabobank.account.service;

import nl.rabobank.account.entity.AccountEntity;
import nl.rabobank.account.entity.AccountTransactionEntity;
import nl.rabobank.account.entity.CardEntity;
import nl.rabobank.account.exception.AccountException;
import nl.rabobank.account.exception.CardException;
import nl.rabobank.account.model.CardStatus;
import nl.rabobank.account.model.CardTypeEnum;
import nl.rabobank.account.model.TransactionTypeEnum;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import nl.rabobank.account.repository.AccountRepository;
import nl.rabobank.account.repository.CardRepository;
import nl.rabobank.account.repository.TransactionRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.Month;

@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CreditCardServiceTest {

    @InjectMocks
    private CreditCardService creditCardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    LocalDateTime currentLocalDate = LocalDateTime.of(2025, Month.APRIL, 12, 0, 0, 0);
    MockedStatic<LocalDateTime> topDateTimeUtilMock = Mockito.mockStatic(LocalDateTime.class);

    @BeforeAll
    public void setup() {
        ReflectionTestUtils.setField(creditCardService, "creditCardAmountPercent", 1.0);
        topDateTimeUtilMock.when(LocalDateTime::now).thenReturn(currentLocalDate);
    }

    @AfterAll
    public void after() {
        topDateTimeUtilMock.close();
    }

    @Test
    public void withdraw_ok() {
        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder().cardNumber(111L).amount(90.0).build();
        CardEntity cardEntity = getCardEntity();
        AccountEntity accountEntity = getAccountEntity();

        BDDMockito.when(cardRepository.findByNumber(111L)).thenReturn(Mono.just(cardEntity));
        BDDMockito.when(accountRepository.findByIban("RABO123")).thenReturn(Mono.just(accountEntity));

        AccountEntity accountEntityUpdated = accountEntity.toBuilder().balance(9.099999999999994).build();

        BDDMockito.when(accountRepository.save(accountEntityUpdated)).thenReturn(Mono.just(accountEntity));
        BDDMockito.when(transactionRepository.save(getWithdrawAccountTransactionEntity(currentLocalDate)))
                .thenReturn(Mono.just(getWithdrawAccountTransactionEntity(currentLocalDate)));

        Mono<Void> result = creditCardService.withdraw(withdrawalRequest);
        StepVerifier.create(result).verifyComplete();  // Verifies the sequence completes successfully
    }

    @Test
    public void withdraw_not_enough_balance() {
        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder().cardNumber(111L).amount(900000.0).build();
        CardEntity cardEntity = getCardEntity();
        AccountEntity accountEntity = getAccountEntity();

        BDDMockito.when(cardRepository.findByNumber(111L)).thenReturn(Mono.just(cardEntity));
        BDDMockito.when(accountRepository.findByIban("RABO123")).thenReturn(Mono.just(accountEntity));

        StepVerifier.create(creditCardService.withdraw(withdrawalRequest))
                .expectSubscription()
                .expectError(AccountException.class)
                .verify();
    }

    @Test
    public void withdraw_account_not_found() {
        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder().cardNumber(111L).amount(0.0).build();
        BDDMockito.when(cardRepository.findByNumber(111L))
                .thenReturn(Mono.error(new CardException("CArd not fount", HttpStatus.INTERNAL_SERVER_ERROR)));

        StepVerifier.create(creditCardService.withdraw(withdrawalRequest))
                .expectSubscription()
                .expectError(CardException.class)
                .verify();
    }

    @Test
    void transfer() {
        TransferRequest transferRequest = TransferRequest.builder()
                .originAccountNumber("RABO123").amount(90.0).targetAccountNumber("RABO999").build();

        AccountEntity originAccount = getAccountEntity();
        AccountEntity targetAccount = getAccountEntity().toBuilder().iban("RABO999").build();

        //Verify
        BDDMockito.when(accountRepository.findByIban(transferRequest.getTargetAccountNumber()))
                .thenReturn(Mono.just(targetAccount));

        BDDMockito.when(accountRepository.findByIban(transferRequest.getOriginAccountNumber()))
                .thenReturn(Mono.just(originAccount));

        AccountEntity originAccountUpdated = originAccount.toBuilder().balance(9.099999999999994).build();
        BDDMockito.when(accountRepository.save(originAccountUpdated)).thenReturn(Mono.just(originAccountUpdated));

        BDDMockito.when(accountRepository.findByIban(transferRequest.getTargetAccountNumber()))
                .thenReturn(Mono.just(targetAccount));

        Double amount2 = targetAccount.getBalance() + transferRequest.getAmount();
        AccountEntity targetAccountUpdated = targetAccount.toBuilder().balance(amount2).build();
        BDDMockito.when(accountRepository.save(targetAccountUpdated)).thenReturn(Mono.just(targetAccountUpdated));

        BDDMockito.when(transactionRepository.save(getTranferAccountTransactionEntity(currentLocalDate)))
                .thenReturn(Mono.just(getTranferAccountTransactionEntity(currentLocalDate)));

        Mono<Void> result = creditCardService.transfer(transferRequest);
        StepVerifier.create(result).verifyComplete();  // Verifies the sequence completes successfully

    }

    @Test
    void transfer_not_enough_balance() {
        TransferRequest transferRequest = TransferRequest.builder()
                .originAccountNumber("RABO123").amount(190.0).targetAccountNumber("RABO999").build();

        AccountEntity originAccount = getAccountEntity();
        AccountEntity targetAccount = getAccountEntity().toBuilder().iban("RABO999").build();

        //Verify
        BDDMockito.when(accountRepository.findByIban(transferRequest.getTargetAccountNumber()))
                .thenReturn(Mono.just(targetAccount));

        BDDMockito.when(accountRepository.findByIban(transferRequest.getOriginAccountNumber()))
                .thenReturn(Mono.just(originAccount));

        StepVerifier.create(creditCardService.transfer(transferRequest))
                .expectSubscription()
                .expectError(AccountException.class)
                .verify();
    }

    @Test
    void transfer_account_not_found() {
        TransferRequest transferRequest = TransferRequest.builder()
                .originAccountNumber("RABO123").amount(190.0).targetAccountNumber("RABO999").build();

        //Verify
        BDDMockito.when(accountRepository.findByIban(transferRequest.getTargetAccountNumber()))
                .thenReturn(Mono.error(new AccountException("Account not fount", HttpStatus.INTERNAL_SERVER_ERROR)));

        StepVerifier.create(creditCardService.transfer(transferRequest))
                .expectSubscription()
                .expectError(AccountException.class)
                .verify();
    }

    private CardEntity getCardEntity() {
        return CardEntity.builder().iban("RABO123")
                .type(CardTypeEnum.CREDIT_CARD).number(111L).id(1L)
                .status(CardStatus.ACTIVE).build();
    }

    private AccountEntity getAccountEntity() {
        return AccountEntity.builder().id(1L).balance(100.0).iban("RABO123").build();
    }

    private AccountTransactionEntity getWithdrawAccountTransactionEntity(LocalDateTime currentLocalDate) {
        return AccountTransactionEntity
                .builder()
                .originAccountNumber("RABO123")
                .targetAccountNumber(null)
                .amount(90.0)
                .percentage(0.9)
                .type(TransactionTypeEnum.WITHDRAWAL)
                .cardType(CardTypeEnum.CREDIT_CARD)
                .date(currentLocalDate)
                .build();
    }

    private AccountTransactionEntity getTranferAccountTransactionEntity(LocalDateTime currentLocalDate) {
        return AccountTransactionEntity
                .builder()
                .originAccountNumber("RABO123")
                .targetAccountNumber("RABO999")
                .amount(90.0)
                .percentage(0.9)
                .type(TransactionTypeEnum.TRANSFER)
                .cardType(CardTypeEnum.CREDIT_CARD)
                .date(currentLocalDate)
                .build();
    }
}