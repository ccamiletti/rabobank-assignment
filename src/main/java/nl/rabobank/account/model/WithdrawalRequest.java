package nl.rabobank.account.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class WithdrawalRequest {

    @NonNull
    private final Long cardNumber;

    @NonNull
    private final Double amount;

}
