package nl.rabobank.account.entity;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import nl.rabobank.account.model.AccountStatus;
import nl.rabobank.account.model.CardTypeEnum;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Table("card")
public class CardEntity {

    @Id
    private final Long id;

    @Column("number")
    private final Long number;

    @Column("iban")
    private final String iban;

    @Column("type")
    private final CardTypeEnum type;

    @Column("user_id")
    private final Long userId;

    @Column("status")
    private final AccountStatus status;



}
