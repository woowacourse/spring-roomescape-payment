package roomescape.reservation.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import roomescape.common.exception.ErrorCode;

@AllArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_IN_PAST(HttpStatus.BAD_REQUEST, "과거 시점에 예약할 수 없습니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 슬롯에 이미 예약이 존재합니다."),
    INVALID_RESERVATION_APPROVAL(HttpStatus.INTERNAL_SERVER_ERROR, "예약 승인에 실패하였습니다."),
    RESERVATION_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "이미 승인된 예약입니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
