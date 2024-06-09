package roomescape.service.reservation.module;

import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Component;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.Status;
import roomescape.exception.ErrorCode;
import roomescape.exception.RoomEscapeException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.util.DateUtil;

@Component
public class ReservationValidator {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public ReservationValidator(ReservationRepository reservationRepository, PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public void validateReservationAvailability(Reservation reservation) {
        validateUnPassedDate(reservation, ErrorCode.RESERVATION_NOT_REGISTER_BY_PAST_DATE);
        validateReservationNotDuplicate(reservation);
    }

    public void validateWaitingAddable(Reservation reservation) {
        validateWaitingReservation(reservation);
        validateUnPassedDate(reservation, ErrorCode.RESERVATION_NOT_WAITING_BY_PAST_DATE);
        validateWaitingDuplicate(reservation);
    }

    public void validatePaymentAvailability(Long reservationId) {
        if (paymentRepository.existsByReservationId(reservationId)) {
            throw new RoomEscapeException(
                    ErrorCode.RESERVATION_ALREADY_PAYMENT,
                    "reservation_id = " + reservationId
            );
        }
    }

    public void validateReservationCancellation(Reservation reservation) {
        validateUnPassedDate(reservation, ErrorCode.RESERVATION_NOT_DELETE_BY_PAST_DATE);
    }

    public void validateApproval(Reservation reservation) {
        validateWaitingStatus(reservation);
        validateReservationAvailability(reservation);
    }

    public void validateWaitingStatus(Reservation reservation) {
        if (!reservation.isWaiting()) {
            throw new RoomEscapeException(
                    ErrorCode.RESERVATION_NOT_WAITING_STATUS,
                    "예약 정보 = " + reservation
            );
        }
    }

    public void validatePaymentPendingStatus(Reservation reservation) {
        if (!reservation.isPaymentPending()) {
            throw new RoomEscapeException(
                    ErrorCode.RESERVATION_NOT_PAYMENT_PENDING_STATUS,
                    "예약 정보 = " + reservation
            );
        }
    }

    private void validateUnPassedDate(Reservation reservation, ErrorCode errorCode) {
        LocalDate date = reservation.getDate();
        LocalTime time = reservation.getTime().getStartAt();
        if (DateUtil.isPastDateTime(date, time)) {
            throw new RoomEscapeException(
                    errorCode,
                    "예약 시간 = " + date + " " + time
            );
        }
    }

    private void validateWaitingReservation(Reservation reservation) {
        if (reservation.isReserved()) {
            throw new IllegalArgumentException(
                    "[ERROR] 확정된 예약은 대기가 불가능합니다.",
                    new Throwable("reservation_id : " + reservation.getId())
            );
        }
    }

    private void validateReservationNotDuplicate(Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeIdAndStatus(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                Status.RESERVED)
        ) {
            throw new RoomEscapeException(
                    ErrorCode.RESERVATION_NOT_REGISTER_BY_DUPLICATE,
                    "생성 예약 정보 = " + reservation
            );
        }
    }

    private void validateWaitingDuplicate(Reservation reservation) {
        if (reservationRepository.existsByDateAndTimeIdAndThemeIdAndMemberId(
                reservation.getDate(),
                reservation.getTime().getId(),
                reservation.getTheme().getId(),
                reservation.getMember().getId())
        ) {
            throw new RoomEscapeException(
                    ErrorCode.WAITING_NOT_REGISTER_BY_DUPLICATE,
                    "생성 예약 대기 정보 = " + reservation
            );
        }

    }
}
