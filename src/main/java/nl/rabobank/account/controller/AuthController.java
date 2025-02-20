package nl.rabobank.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.model.LoginRequest;
import nl.rabobank.account.model.LoginResponse;
import nl.rabobank.account.model.SignUpRequest;
import nl.rabobank.account.service.AuthenticationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public Mono<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.signIn(loginRequest);
    }

    @PostMapping("/signUp")
    public Mono<Void> signUp(@RequestBody SignUpRequest signUpRequest ) {
        return authenticationService.signUp(signUpRequest);
    }

}
