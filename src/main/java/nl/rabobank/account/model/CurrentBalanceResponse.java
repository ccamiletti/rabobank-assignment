package nl.rabobank.account.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class CurrentBalanceResponse {

    private final String accountNumber;
    private final Double balance;

}
