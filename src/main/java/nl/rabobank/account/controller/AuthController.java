package nl.rabobank.account.controller;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.model.LoginResponse;
import nl.rabobank.account.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public Mono<LoginResponse> login(@RequestParam String userName, @RequestParam String password) {
        return authenticationService.signIn(userName, password);
    }

}
