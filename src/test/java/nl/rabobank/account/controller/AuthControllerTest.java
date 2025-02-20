package nl.rabobank.account.controller;

import nl.rabobank.account.entity.UserEntity;
import nl.rabobank.account.model.LoginRequest;
import nl.rabobank.account.model.LoginResponse;
import nl.rabobank.account.model.SignUpRequest;
import nl.rabobank.account.repository.UserRepository;
import nl.rabobank.account.service.AuthenticationService;
import nl.rabobank.account.service.UserAuthenticationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = AuthController.class, excludeAutoConfiguration = ReactiveSecurityAutoConfiguration.class)
public class AuthControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    AuthenticationService authService;

    @Test
    public void shouldSignUp() {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .name("test")
                .lastName("last")
                .password("pass")
                .userName("user")
                .build();
        when(authService.signUp(signUpRequest)).thenReturn(Mono.empty());

        webClient.method(HttpMethod.POST).uri("/signUp")
                .bodyValue(signUpRequest)
                .exchange()
                .expectStatus()
                .isOk();

        StepVerifier.create(authService.signUp(signUpRequest))
                .expectNextCount(0)
                .verifyComplete();

    }

    @Test
    public void shouldLogin() {
        LoginRequest loginRequest = LoginRequest.builder().userName("user").password("pass").build();
        LoginResponse loginResponse = LoginResponse.builder().userName("user").token("token").build();
        when(authService.signIn(loginRequest)).thenReturn(Mono.just(loginResponse));
        webClient.method(HttpMethod.GET).uri("/login")
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(LoginResponse.class);

        StepVerifier.create(authService.signIn(loginRequest))
                .expectNext(LoginResponse.builder().userName("user").token("token").build())
                .verifyComplete();

    }

}
