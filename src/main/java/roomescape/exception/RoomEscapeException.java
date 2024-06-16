package roomescape.exception;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;

@Tag(name = "방탈출 예외", description = "방탈출 예외는 커스텀 예외 코드를 가져야 한다.")
public class RoomEscapeException extends RuntimeException {

    private final ExceptionCode exceptionCode;

    public RoomEscapeException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public HttpStatus getHttpStatus() {
        return exceptionCode.getHttpStatus();
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
