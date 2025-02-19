package nl.rabobank.account.exception;

import org.springframework.http.HttpStatus;

public class AccountException extends RuntimeException {

    public HttpStatus status;

    public AccountException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}