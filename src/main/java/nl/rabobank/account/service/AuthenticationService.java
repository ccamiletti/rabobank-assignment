package nl.rabobank.account.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.exception.UserException;
import nl.rabobank.account.model.CurrentUserResponse;
import nl.rabobank.account.model.LoginRequest;
import nl.rabobank.account.model.LoginResponse;
import nl.rabobank.account.model.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public Mono<LoginResponse> authenticate(LoginRequest loginRequest) {
        return reactiveUserDetailsService
                .findByUsername(loginRequest.getUserName())
                .map(userDetails -> getLoginResponse(loginRequest, userDetails));
    }

    public Mono<CurrentUserResponse> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext().map(securityContext -> {
            UserPrincipal principal = (UserPrincipal) securityContext.getAuthentication().getPrincipal();
            return CurrentUserResponse.builder()
                    .name(principal.getName())
                    .userName(principal.getUsername())
                    .build();
        }).switchIfEmpty(Mono.error(new UserException("NO USER IN CONTEXT", HttpStatus.BAD_REQUEST)));
    }

    private LoginResponse getLoginResponse(LoginRequest loginRequest, UserDetails userDetails) {
        if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            final String token = jwtService.generate(userDetails.getUsername());
            return LoginResponse.builder()
                    .userName(userDetails.getUsername())
                    .token(token).build();
        } else {
            throw new UserException("BAD CREDENTIALS", HttpStatus.BAD_REQUEST);
        }
    }

}
