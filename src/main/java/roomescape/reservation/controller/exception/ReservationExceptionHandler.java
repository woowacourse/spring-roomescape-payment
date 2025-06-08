package roomescape.reservation.controller.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static roomescape.reservation.controller.response.ReservationErrorCode.ALREADY_RESERVATION;
import static roomescape.reservation.controller.response.ReservationErrorCode.PAST_RESERVATION;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import roomescape.global.response.ApiResponse;
import roomescape.reservation.exception.InAlreadyReservationException;
import roomescape.reservation.exception.InvalidStatusTransitionException;
import roomescape.reservation.exception.PastReservationException;

@RestControllerAdvice
@Slf4j
public class ReservationExceptionHandler {

    @ExceptionHandler(PastReservationException.class)
    public ResponseEntity<ApiResponse<Void>> handlePastReservationException(HttpServletRequest request,
                                                                            PastReservationException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        return ResponseEntity
                .status(UNPROCESSABLE_ENTITY)
                .body(ApiResponse.fail(PAST_RESERVATION));
    }

    @ExceptionHandler(InAlreadyReservationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAlreadyReservationException(HttpServletRequest request,
                                                                               InAlreadyReservationException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        return ResponseEntity
                .status(CONFLICT)
                .body(ApiResponse.fail(ALREADY_RESERVATION));
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidStatusTransitionException(HttpServletRequest request,
                                                                                    InvalidStatusTransitionException e) {
        log.info("{} | {} {}", request.getAttribute("traceId"), e.getClass(), e.getMessage());
        return ResponseEntity
                .status(UNPROCESSABLE_ENTITY)
                .body(ApiResponse.fail(ALREADY_RESERVATION));
    }
}
