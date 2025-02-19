package nl.rabobank.account.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.rabobank.account.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class UserPrincipal implements UserDetails {

    private Long id;

    private String username;

    private String password;

    private String name;

    private String lastName;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(UserEntity userEntity) {
        return UserPrincipal.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .build();

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
