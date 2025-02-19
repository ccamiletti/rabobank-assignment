package nl.rabobank.account.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.entity.UserEntity;
import nl.rabobank.account.exception.UserException;
import nl.rabobank.account.model.SignUpRequest;
import nl.rabobank.account.model.UserPrincipal;
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
public class UserAuthenticationService implements ReactiveUserDetailsService {

    public final UserRepository userRepository;

    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.findByUsername(email)
                .map(this::getUserDetails)
                .onErrorMap((e) -> {
                    log.error("ERROR findByUsername. User {} does not exist", email);
                    throw new UserException("INTERNAL SERVER ERROR", HttpStatus.BAD_REQUEST);
                }).switchIfEmpty(Mono.error(new UserException("ERROR USER NOT FOUND", HttpStatus.BAD_REQUEST)));
    }

    private UserDetails getUserDetails(UserEntity userEntity) {
        return UserPrincipal.create(userEntity);
    }

}
