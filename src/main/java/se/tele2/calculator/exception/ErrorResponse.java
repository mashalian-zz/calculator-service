package se.tele2.calculator.exception;

import lombok.NonNull;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        @NonNull HttpStatus status,
        @NonNull String message
) {
}
