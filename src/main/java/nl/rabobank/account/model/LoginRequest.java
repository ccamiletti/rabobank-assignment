package nl.rabobank.account.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {

    @NonNull
    private final String userName;

    @NonNull
    private final String password;

}
