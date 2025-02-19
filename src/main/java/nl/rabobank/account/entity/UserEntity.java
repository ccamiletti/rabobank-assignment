package nl.rabobank.account.entity;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@RequiredArgsConstructor
@Builder
@Table("users")
public class UserEntity {

    @Id
    private final Long id;

    @Column("username")
    private final String username;

    @Column("name")
    private final String name;

    @Column("lastname")
    private final String lastName;

    @Column("password")
    private final String password;

}
