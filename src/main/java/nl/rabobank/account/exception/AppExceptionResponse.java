package nl.rabobank.account.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class AppExceptionResponse {

    private Instant timestamp;
    private HttpStatus status;
    private String message;

}
