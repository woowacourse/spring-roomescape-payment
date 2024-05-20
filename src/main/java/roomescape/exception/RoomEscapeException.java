package roomescape.exception;


import org.springframework.http.HttpStatus;
import roomescape.payment.TossError;

public class RoomEscapeException extends RuntimeException {
    private String errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;

    public RoomEscapeException(RoomEscapeErrorCode roomEscapeErrorCode) {
        this.errorCode = roomEscapeErrorCode.getCode();
        this.errorMessage = roomEscapeErrorCode.getMessage();
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public RoomEscapeException(RoomEscapeErrorCode roomEscapeErrorCode, HttpStatus httpStatus) {
        this.errorCode = roomEscapeErrorCode.getCode();
        this.errorMessage = roomEscapeErrorCode.getMessage();
        this.httpStatus = httpStatus;
    }

    public RoomEscapeException(TossError tossError, HttpStatus httpStatus) {
        this.errorCode = RoomEscapeErrorCode.PAYMENT_FAILED.getCode();
        this.errorMessage = getErrorMessageFrom(tossError);
        this.httpStatus = httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    private String getErrorMessageFrom(TossError error) {
        return "토스 결제 실패 : "+ error.code() + " " + error.message();
    }
}
