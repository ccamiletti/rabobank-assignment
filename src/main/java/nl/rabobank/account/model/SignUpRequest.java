package nl.rabobank.account.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class SignUpRequest {

    @NonNull
    private final String userName;

    @NonNull
    private final String name;

    @NonNull
    private final String lastName;

    @NonNull
    private final String password;

}
