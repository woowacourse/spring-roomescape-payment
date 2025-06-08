package roomescape.exception;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.domain.auth.TokenBody;
import roomescape.exception.custom.status.CustomException;
import roomescape.infrastructure.auth.JwtProvider;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final HttpServletRequest httpServletRequest;
    private final JwtProvider jwtProvider;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlerIllegalArgument(
            final CustomException e
    ) {
        log.warn(formatLoggingMessage(e.getMessage()));
        return ResponseEntity.status(e.getStatusValue())
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("잘못된 형식의 요청입니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleNotValidRequest(
            final MethodArgumentNotValidException e,
            final BindingResult bindingResult
    ) {
        final String notValidField = generateNotValidFieldNames(bindingResult);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(notValidField + "의 값이 잘못된 형식입니다."));
    }

    private String generateNotValidFieldNames(final BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleNotCaughtExceptions(
            final Exception e
    ) {
        log.error(formatLoggingMessage(e.getMessage()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버에서 예기치 못한 예외가 발생하였습니다."));
    }

    private String formatLoggingMessage(final String exceptionMessage) {
        final String ip = httpServletRequest.getRemoteAddr();
        final int port = httpServletRequest.getRemotePort();
        final String tokenBody = getTokenBody();

        return String.format("%s %s %s %s %s",
                ip, port, httpServletRequest.getRequestURI(), tokenBody, exceptionMessage
        );
    }

    private String getTokenBody() {
        final String token = Optional.ofNullable(httpServletRequest.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies)
                        .filter(c -> "token".equals(c.getName()))
                        .findFirst())
                .map(Cookie::getValue)
                .orElse("null");

        if (!jwtProvider.isValidToken(token)) {
            return token;
        }

        final TokenBody tokenBody = jwtProvider.extractBody(token);
        return String.format("%s %s %s",
                tokenBody.role(), tokenBody.name(), tokenBody.email()
        );
    }
}
