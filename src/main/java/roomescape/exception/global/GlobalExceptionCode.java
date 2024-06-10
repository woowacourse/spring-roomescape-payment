package roomescape.exception.global;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import roomescape.exception.ExceptionCode;

@Tag(name = "글로벌 예외 코드", description = "특정 기능에 종속되지 않는 예외 코드 모음")
public enum GlobalExceptionCode implements ExceptionCode {

    METHOD_ARGUMENT_TYPE_INVALID(HttpStatus.BAD_REQUEST, "타입이 일치하지 않습니다."),
    HTTP_RESPONSE_DATA_INVALID(HttpStatus.BAD_REQUEST, "http 응답 body의 데이터에서 문제가 발생했습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버에서 에러가 발생했습니다."),
    JSON_DATA_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "Json 변환에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    GlobalExceptionCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
