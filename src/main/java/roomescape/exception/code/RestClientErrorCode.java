package roomescape.exception.code;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;

public enum RestClientErrorCode implements ErrorCode {

    CONNECTION_ERROR(
            INTERNAL_SERVER_ERROR, "외부 API 서버에 연결할 수 없습니다."),
    SOCKET_TIMEOUT(
            INTERNAL_SERVER_ERROR, "외부 API 응답 시간이 초과되었습니다"),
    EXTERNAL_API_ERROR(
            INTERNAL_SERVER_ERROR, "외부 API와 통신 중 오류가 발생했습니다."),
    RESPONSE_PARSING_ERROR(
            INTERNAL_SERVER_ERROR, "외부 API 응답을 변환하는 과정에서 오류가 발생했습니다.");

    private final HttpStatus statusCode;
    private final String message;

    RestClientErrorCode(HttpStatus statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }


    @Override
    public HttpStatus getHttpStatus() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
