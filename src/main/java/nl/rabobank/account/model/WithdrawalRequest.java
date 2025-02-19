package nl.rabobank.account.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class WithdrawalRequest {

    @NonNull
    private final Long cardNumber;

    @NonNull
    private final Double amount;

}
