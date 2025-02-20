package nl.rabobank.account.repository;

import nl.rabobank.account.entity.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@DataR2dbcTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    DatabaseClient database;

    @BeforeEach
    void setUp() {
        Hooks.onOperatorDebug();
        var statements = Arrays.asList(//
                "DROP TABLE IF EXISTS account;",
                "CREATE TABLE account ( id SERIAL PRIMARY KEY, iban VARCHAR(25) NOT NULL, balance DECIMAL(10,2) NOT NULL);");
        statements.forEach(it -> database.sql(it) //
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNextCount(1) //
                .verifyComplete());
    }

    @Test
    void executesFindAll() {
        AccountEntity account1 = AccountEntity.builder().iban("RABO1111").balance(10000.0).build();
        insertCustomers(account1);
        StepVerifier.create(accountRepository.findAll())
                .expectNextMatches(result->result.getIban().equalsIgnoreCase("RABO1111"))
                .verifyComplete();
    }

    private void insertCustomers(AccountEntity... customers) {
        this.accountRepository.saveAll(Arrays.asList(customers))//
                .as(StepVerifier::create) //
                .expectNextCount(1) //
                .verifyComplete();
    }
}
