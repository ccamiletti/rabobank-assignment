package nl.rabobank.account.controller;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.model.CurrentUserResponse;
import nl.rabobank.account.service.AuthenticationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    @GetMapping("/principal")
    public Mono<CurrentUserResponse> gtePrincipal() {
        return authenticationService.getCurrentUser();
    }

}
