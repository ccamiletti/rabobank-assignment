package nl.rabobank.account.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.model.LoginRequest;
import nl.rabobank.account.model.LoginResponse;
import nl.rabobank.account.model.SignUpRequest;
import nl.rabobank.account.service.AuthenticationService;
import nl.rabobank.account.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.authenticate(loginRequest).map(ResponseEntity::ok);
    }

    @PostMapping("/signUp")
    public Mono<Void> signUp(@RequestBody SignUpRequest signUpRequest ) {
        return userService.signUp(signUpRequest);
    }

}
