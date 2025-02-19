package nl.rabobank.account.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import nl.rabobank.account.model.CardTypeEnum;
import nl.rabobank.account.model.TransactionTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder
@Table("account_transaction")
public class AccountTransactionEntity {

    @Id
    private final Long id;

    @Column("origin_account_number")
    private final String originAccountNumber;

    @Column("target_account_number")
    private final String targetAccountNumber;

    @Column("amount")
    private final Double amount;

    @Column("percentage")
    private final Double percentage;

    @Column("type")
    private final TransactionTypeEnum type;

    @Column("card_type")
    private final CardTypeEnum cardType;

    @Column("date")
    private final LocalDateTime date;



}
