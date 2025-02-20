package nl.rabobank.account.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rabobank.account.entity.UserEntity;
import nl.rabobank.account.model.UserPrincipal;
import nl.rabobank.account.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class UserAuthenticationService implements ReactiveUserDetailsService {

    public final UserRepository userRepository;

    public Mono<UserDetails> findByUsername(String userName) {
        return userRepository.findByUsername(userName)
                .map(this::getUserDetails);
    }

    private UserDetails getUserDetails(UserEntity userEntity) {
        return UserPrincipal.create(userEntity);
    }

}
