package nl.rabobank.account.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.entity.UserEntity;
import nl.rabobank.account.exception.UserException;
import nl.rabobank.account.model.SignUpRequest;
import nl.rabobank.account.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    public final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<Void> signUp(SignUpRequest signUpRequest) {
        return userRepository.findByUsername(signUpRequest.getUserName())
                .handle((userEntity, sink) -> {
                    log.error("Error creating User {}. already exists", userEntity.getUsername());
                    sink.error(new UserException("INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR));
        }).switchIfEmpty(userRepository.save(toEntity(signUpRequest))).then();
    }

    private UserEntity toEntity(SignUpRequest signUpRequest) {
        return UserEntity.builder()
                .username(signUpRequest.getUserName())
                .name(signUpRequest.getName())
                .lastName(signUpRequest.getLastName())
                .password(passwordEncoder.encode(signUpRequest.getPassword())).build();
    }

}
