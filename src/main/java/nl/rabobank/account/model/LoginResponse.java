package nl.rabobank.account.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class LoginResponse {

    private final String userName;
    private final String token;

}
