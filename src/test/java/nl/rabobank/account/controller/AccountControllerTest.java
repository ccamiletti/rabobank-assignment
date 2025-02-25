package nl.rabobank.account.controller;

import nl.rabobank.account.model.CurrentBalanceResponse;
import nl.rabobank.account.model.TransferRequest;
import nl.rabobank.account.model.WithdrawalRequest;
import nl.rabobank.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AccountController.class, excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    AccountService accountService;

    @Test
    void getBalance() {
        Flux<CurrentBalanceResponse> currentBalanceResponseFlux = Flux.just(CurrentBalanceResponse.builder().build(), CurrentBalanceResponse.builder().build());
        when(accountService.getBalance()).thenReturn(currentBalanceResponseFlux);
        webClient.method(HttpMethod.GET).uri("/account/all")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CurrentBalanceResponse.class);
    }

    @Test
    void withdraw() {
        WithdrawalRequest withdrawalRequest = WithdrawalRequest.builder()
                .amount(10.0).cardNumber(1111L).build();
        when(accountService.withdraw(withdrawalRequest)).thenReturn(Mono.empty().then());
        webClient.method(HttpMethod.POST).uri("/account/withdraw")
                .bodyValue(withdrawalRequest)
                .exchange()
                .expectStatus()
                .isOk();

        StepVerifier.create(accountService.withdraw(withdrawalRequest))
                .expectNextCount(0)
                .verifyComplete();

    }

    @Test
    void transfer() {
        TransferRequest transferRequest = TransferRequest.builder()
                .targetAccountNumber("RABO123")
                .originAccountNumber("RABO777")
                .amount(-10.0)
                .build();
        when(accountService.transfer(transferRequest)).thenReturn(Mono.empty().then());
        webClient.method(HttpMethod.POST).uri("/account/transfer")
                .bodyValue(transferRequest)
                .exchange()
                .expectStatus()
                .isOk();

        StepVerifier.create(accountService.transfer(transferRequest))
                .expectNextCount(0)
                .verifyComplete();
    }

}