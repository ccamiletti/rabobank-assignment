package nl.rabobank.account.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.entity.UserEntity;
import nl.rabobank.account.exception.UserException;
import nl.rabobank.account.model.LoginRequest;
import nl.rabobank.account.model.LoginResponse;
import nl.rabobank.account.model.SignUpRequest;
import nl.rabobank.account.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final ReactiveUserDetailsService reactiveUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    public final UserRepository userRepository;

    @Transactional
    public Mono<Void> signUp(SignUpRequest signUpRequest) {
        return reactiveUserDetailsService.findByUsername(signUpRequest.getUserName())
                .flatMap(userDetails -> Mono.defer(() -> {
                    log.error("Error creating User {}. already exists", signUpRequest.getUserName());
                    throw new UserException("internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
                })).switchIfEmpty(userRepository.save(toEntity(signUpRequest)))
                .then();
    }

    public Mono<LoginResponse> signIn(LoginRequest loginRequest) {
        return reactiveUserDetailsService.findByUsername(loginRequest.getUserName())
                .map(userDetails -> getLoginResponse(loginRequest, userDetails));
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

    private UserEntity toEntity(SignUpRequest signUpRequest) {
        return UserEntity.builder()
                .username(signUpRequest.getUserName())
                .name(signUpRequest.getName())
                .lastName(signUpRequest.getLastName())
                .password(passwordEncoder.encode(signUpRequest.getPassword())).build();
    }


}
