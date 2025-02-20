package nl.rabobank.account.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class LoginRequest {

    @NonNull
    private final String userName;

    @NonNull
    private final String password;

}
