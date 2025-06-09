package roomescape.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.exception.custom.reason.member.MemberEmailConflictException;
import roomescape.exception.custom.reason.order.OrderNotMatchException;
import roomescape.exception.custom.reason.reservation.ReservationConflictException;
import roomescape.exception.custom.reason.reservation.ReservationNotExistsScheduleException;
import roomescape.exception.custom.reason.reservation.ReservationPastDateException;
import roomescape.exception.custom.reason.reservationpayment.ReservationPaymentConfirmException;
import roomescape.exception.custom.reason.reservationtime.ReservationTimeConflictException;
import roomescape.exception.custom.reason.schedule.ScheduleConflictException;
import roomescape.exception.custom.reason.waiting.WaitingPastScheduleException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
@Slf4j
public class LoggingExceptionHandler {

    @ExceptionHandler(OrderNotMatchException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotMatchException(OrderNotMatchException e) {
        log.warn("EVENT: RESERVATION_CREATE_FAILED_ORDER_NOT_MATCH, orderId={}", e.getOrderId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ReservationPastDateException.class)
    public ResponseEntity<ErrorResponse> handleReservationPastDateException(ReservationPastDateException e) {
        log.warn("EVENT: RESERVATION_CREATE_FAILED - PAST_SCHEDULE, date={}, time={}",
                e.getDate(),
                e.getStartAt());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ReservationNotExistsScheduleException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotExistsScheduleException(ReservationNotExistsScheduleException e) {
        log.warn("EVENT: WAITING_CREATE_FAILED - RESERVATION_NOT_EXISTS, date={}, time={}",
                e.getDate(),
                e.getStartAt());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(WaitingPastScheduleException.class)
    public ResponseEntity<ErrorResponse> handleWaitingPastScheduleException(WaitingPastScheduleException e) {
        log.warn("EVENT: WAITING_CREATE_FAILED - PAST_SCHEDULE, date={}, time={}",
                e.getDate(),
                e.getStartAt());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ReservationConflictException.class)
    public ResponseEntity<ErrorResponse> handleReservationConflictException(ReservationConflictException e) {
        log.warn("RESERVATION_CREATE_FAILED - DUPLICATED, date={}, time={}, themeName={}",
                e.getDate(),
                e.getStartAt(),
                e.getThemeName());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<ErrorResponse> handleScheduleConflictException(ScheduleConflictException e) {
        log.warn("EVENT: SCHEDULE_CREATE_FAILED - DUPLICATED, themeName={}, date={}, time={}",
                e.getThemeName(),
                e.getDate(),
                e.getStartAt());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ReservationPaymentConfirmException.class)
    public ResponseEntity<ErrorResponse> handleReservationPaymentConfirmException(ReservationPaymentConfirmException e) {
        log.warn("EVENT: PAYMENT_CONFIRM_FAILED, reservationId={}, paymentKey={}, orderId={}",
                e.getReservationId(),
                e.getPaymentKey(),
                e.getOrderId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(MemberEmailConflictException.class)
    public ResponseEntity<ErrorResponse> handleMemberEmailConflictException(MemberEmailConflictException e) {
        log.warn("EVENT: MEMBER_SIGNED_UP_FAILED - DUPLICATED");
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(ReservationTimeConflictException.class)
    public ResponseEntity<ErrorResponse> handleReservationTimeConflictException(ReservationTimeConflictException e) {
        log.warn("EVENT: RESERVATION_TIME_CREATE_FAILED - DUPLICATED, time={}",
                e.getStartAt());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }
}
