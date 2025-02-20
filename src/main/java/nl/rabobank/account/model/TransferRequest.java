package nl.rabobank.account.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class TransferRequest {

    @NonNull
    private final String originAccountNumber;

    @NonNull
    private final String targetAccountNumber;

    @NonNull
    private final Double amount;


}
