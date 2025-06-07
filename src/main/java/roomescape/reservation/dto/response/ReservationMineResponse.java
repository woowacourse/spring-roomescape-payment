package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationWithPayment;
import roomescape.reservation.dto.WaitingWithRank;

public record ReservationMineResponse(Long reservationId, String theme, LocalDate date, LocalTime time, String status,
                                      String paymentKey, Integer amount, LocalDateTime approvedAt) {

    private static final String RESERVED = "예약";
    private static final String WAITING = "%d번째 예약대기";

    public static ReservationMineResponse from(final ReservationWithPayment reservationWithPayment) {
        Reservation reservation = reservationWithPayment.reservation();
        Payment payment = reservationWithPayment.payment();
        return new ReservationMineResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                RESERVED,
                payment.getPaymentKey(),
                payment.getAmount(),
                payment.getApprovedAt()
        );
    }

    public static ReservationMineResponse from(final Reservation reservation) {
        return new ReservationMineResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                RESERVED,
                null,
                null,
                null
        );
    }

    public static ReservationMineResponse from(final WaitingWithRank waitingWithRank) {
        Reservation reservation = waitingWithRank.waiting().getReservation();
        return new ReservationMineResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                formatRank(waitingWithRank.rank()),
                null,
                null,
                null
        );
    }

    private static String formatRank(final Long rank) {
        return String.format(WAITING, rank);
    }
}
