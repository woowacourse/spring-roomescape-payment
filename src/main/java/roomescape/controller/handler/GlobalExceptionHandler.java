package roomescape.controller.handler;

import io.jsonwebtoken.JwtException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import roomescape.dto.response.ExceptionInfo;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, NumberFormatException.class, BadRequestException.class})
    public ResponseEntity<String> handleBadRequestException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionInfo> handleHttpClientException(HttpClientErrorException exception) {
        ExceptionInfo exceptionInfo = exception.getResponseBodyAs(ExceptionInfo.class);
        return ResponseEntity.badRequest().body(exceptionInfo);
    }
}
