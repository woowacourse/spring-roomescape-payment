package roomescape.web.support;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.AuthenticationException;
import roomescape.exception.AuthorizationException;
import roomescape.exception.PaymentServerException;
import roomescape.exception.RoomEscapeException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RoomEscapeException.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "잘못된 값이 전달되었습니다.")
    })
    public ResponseEntity<String> handleRoomEscapeException(RoomEscapeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자입니다.",
                    content = @Content(examples = @ExampleObject("로그인이 필요합니다."))
            )
    })
    public ResponseEntity<String> handleAuthenticationFailureException(AuthenticationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = AuthorizationException.class)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "403",
                    description = "권한이 없는 사용자입니다.",
                    content = @Content(examples = @ExampleObject("권한이 없습니다."))
            )
    })
    public ResponseEntity<String> handleAuthorizationFailureException(AuthorizationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = PaymentServerException.class)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "500",
                    description = "결제 중 서버에서 문제가 발생했습니다.",
                    content = @Content(examples = @ExampleObject("결제에 실패했어요. 같은 문제가 반복된다면 관리자에게 문의해주세요."))
            )
    })
    public ResponseEntity<String> handlePaymentServerException(PaymentServerException exception) {
        log.error("[Payment Client]", exception);
        return new ResponseEntity<>("결제에 실패했어요. 같은 문제가 반복된다면 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<String> handleValidationException(BindException exception) {
        return new ResponseEntity<>(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpMessageConversionException.class)
    public ResponseEntity<String> handleJsonParsingException() {
        return new ResponseEntity<>("유효하지 않은 필드가 존재합니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 에러입니다.",
                    content = @Content(examples = @ExampleObject("서버 에러입니다."))
            )
    })
    public ResponseEntity<String> handleException(Exception exception) {
        log.error("[Exception]", exception);
        return new ResponseEntity<>("서버 에러입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
