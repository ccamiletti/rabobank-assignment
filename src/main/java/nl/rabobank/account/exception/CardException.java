package nl.rabobank.account.exception;

import org.springframework.http.HttpStatus;

public class CardException extends RuntimeException {

    public HttpStatus status;

    public CardException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}