package nl.rabobank.account.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.exception.UserException;
import nl.rabobank.account.model.LoginResponse;
import nl.rabobank.account.repository.UserRepository;
import org.springframework.http.HttpStatus;
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
    public final UserRepository userRepository;

    public Mono<LoginResponse> signIn(String userName, String password) {
        return reactiveUserDetailsService.findByUsername(userName)
                .switchIfEmpty(Mono.error(new UserException("User Not Found", HttpStatus.INTERNAL_SERVER_ERROR)))
                .map(userDetails -> getLoginResponse(password, userDetails));
    }

    private LoginResponse getLoginResponse(String password, UserDetails userDetails) {
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            final String token = jwtService.generate(userDetails.getUsername());
            return LoginResponse.builder()
                    .userName(userDetails.getUsername())
                    .token(token).build();
        } else {
            throw new UserException("BAD CREDENTIALS", HttpStatus.BAD_REQUEST);
        }
    }
}
