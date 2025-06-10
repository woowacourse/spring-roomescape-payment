package roomescape.dto.reservation.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.reservation.ReservationWithPayment;
import roomescape.dto.reservation.WaitingWithRank;

public record ReservationMineResponse(Long reservationId, String theme, LocalDate date, LocalTime time, String status,
                                      String paymentKey, Integer amount, LocalDateTime approvedAt) {

    private static final String RESERVED = "예약";
    private static final String WAITING = "%d번째 예약대기";

    public static ReservationMineResponse from(final ReservationWithPayment reservationWithPayment) {
        Reservation reservation = reservationWithPayment.reservation();
        Payment payment = reservationWithPayment.payment();
        return new ReservationMineResponse(
                reservation.id(),
                reservation.theme().name(),
                reservation.date(),
                reservation.time().startAt(),
                RESERVED,
                payment.paymentKey(),
                payment.amount(),
                payment.approvedAt()
        );
    }

    public static ReservationMineResponse from(final Reservation reservation) {
        return new ReservationMineResponse(
                reservation.id(),
                reservation.theme().name(),
                reservation.date(),
                reservation.time().startAt(),
                RESERVED,
                null,
                null,
                null
        );
    }

    public static ReservationMineResponse from(final WaitingWithRank waitingWithRank) {
        Reservation reservation = waitingWithRank.waiting().reservation();
        return new ReservationMineResponse(
                reservation.id(),
                reservation.theme().name(),
                reservation.date(),
                reservation.time().startAt(),
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
