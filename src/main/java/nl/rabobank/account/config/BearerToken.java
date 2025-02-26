package nl.rabobank.account.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class BearerToken extends AbstractAuthenticationToken {

    private final String token;

    public BearerToken(String token) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
    }


    @Override
    public String getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

}
