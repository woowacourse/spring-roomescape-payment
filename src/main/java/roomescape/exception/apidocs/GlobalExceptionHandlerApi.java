package roomescape.exception.apidocs;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.exception.custom.PaymentException;
import roomescape.exception.custom.UnauthorizedException;
import roomescape.exception.dto.ErrorResponse;

public interface GlobalExceptionHandlerApi {

    public ResponseEntity<ErrorResponse> handleClientPaymentException(PaymentException e);

    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e);

    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e);

    public ResponseEntity<List<ErrorResponse>> handleValidationException(MethodArgumentNotValidException e);

    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e);

    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "비즈니스 로직 외 서버 오류", content = @Content(mediaType = "application/json",
                    examples = {
                            @ExampleObject(name = "비즈니스 로직 외 서버 오류",
                                    description = "비즈니스 외 서버 오류가 발생하면 제공되는 오류 메시지입니다.", value = """
                                    {
                                        "message": "문제가 발생하였습니다."
                                    }
                                    """)
                    }
            ))
    })
    ResponseEntity<ErrorResponse> handleException(Exception e);
}
