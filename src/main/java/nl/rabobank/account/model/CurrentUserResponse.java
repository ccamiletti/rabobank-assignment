package nl.rabobank.account.model;

import lombok.*;

@Data
@Builder
@RequiredArgsConstructor
public class CurrentUserResponse {

    private final String userName;
    private final String name;

}
