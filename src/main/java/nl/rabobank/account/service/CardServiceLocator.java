package nl.rabobank.account.service;

import lombok.RequiredArgsConstructor;
import nl.rabobank.account.model.CardTypeEnum;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceLocator {

    private final CreditCardService creditCardService;
    private final DebitCardService debitCardService;

    public CardService getService(CardTypeEnum cardType) {
        return switch (cardType) {
            case DEBIT_CARD -> debitCardService;
            case CREDIT_CARD -> creditCardService;
        };
    }
}