package nl.rabobank.account.exception;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Hidden
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<AppExceptionResponse> createUserErrorMessage(UserException userException) {
        return new ResponseEntity<>(
                createResponse(userException.getMessage(), Instant.now(), userException.status), userException.status
        );
    }

    @ExceptionHandler(AccountException.class)
    public ResponseEntity<AppExceptionResponse> createJwtErrorMessage(AccountException accountException) {
        return new ResponseEntity<>(
                createResponse(accountException.getMessage(), Instant.now(), accountException.status), accountException.status
        );
    }

    private AppExceptionResponse createResponse(String message, Instant instant, HttpStatus status) {
        return AppExceptionResponse.builder()
                        .message(message)
                        .timestamp(instant)
                        .status(status).build();
    }

}
