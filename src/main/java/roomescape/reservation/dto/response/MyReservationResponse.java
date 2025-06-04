package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.dto.ReservationWithPayment;

public record MyReservationResponse(Long reservationId,
                                    String theme,
                                    LocalDate date,
                                    LocalTime time,
                                    String reservedStatus,
                                    String paymentKey,
                                    Integer amount) {

    public static MyReservationResponse from(final WaitingWithRank waitingWithRank) {
        return new MyReservationResponse(waitingWithRank.getWaitingId(),
                waitingWithRank.getThemeName(),
                waitingWithRank.getDate(),
                waitingWithRank.getStartAt(),
                waitingWithRank.getRank() + "번째 " + ReservationStatus.WAITING.getName(),
                null,
                null);
    }

    public static MyReservationResponse from(final ReservationWithPayment reservationWithPayment) {
        Reservation reservation = reservationWithPayment.getReservation();
        Optional<Payment> findPayment = reservationWithPayment.getPayment();
        String paymentKey = null;
        Integer amount = null;
        if (findPayment.isPresent()) {
            Payment payment = findPayment.get();
            paymentKey = payment.getPaymentKey();
            amount = payment.getAmount();
        }

        return new MyReservationResponse(reservation.getId(), reservation.getThemeName(),
                reservation.getDate(),
                reservation.getStartAt(),
                ReservationStatus.RESERVED.getName(),
                paymentKey,
                amount);
    }
}
