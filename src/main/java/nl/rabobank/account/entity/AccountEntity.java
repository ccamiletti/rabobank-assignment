package nl.rabobank.account.entity;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import nl.rabobank.account.model.AccountStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Table("account")
public class AccountEntity {

    @Id
    private final Long id;

    @Column("iban")
    private final String iban;

    @Column("balance")
    private final Double balance;

}
