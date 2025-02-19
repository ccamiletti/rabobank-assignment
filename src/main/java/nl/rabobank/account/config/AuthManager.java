package nl.rabobank.account.config;

import lombok.AllArgsConstructor;
import nl.rabobank.account.model.UserPrincipal;
import nl.rabobank.account.service.JWTService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class AuthManager implements ReactiveAuthenticationManager {

    private final JWTService jwtService;
    private final ReactiveUserDetailsService reactiveUserDetailsService;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .cast(BearerToken.class)
                .map(auth -> jwtService.getUserName(auth.getCredentials()))
                .flatMap(reactiveUserDetailsService::findByUsername)
                .map(userDetails -> {
                    UserPrincipal userPrincipal = (UserPrincipal) userDetails;
                    return new UsernamePasswordAuthenticationToken(userPrincipal, userPrincipal.getPassword(),
                            userPrincipal.getAuthorities());
                });
    }

}
