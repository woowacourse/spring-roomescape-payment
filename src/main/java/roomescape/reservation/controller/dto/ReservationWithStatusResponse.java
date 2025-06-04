package roomescape.reservation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import roomescape.payment.controller.dto.PaymentWebResponse;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.domain.ReservationWait;
import roomescape.reservation.service.dto.ReservationWaitWithRankResponse;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationWithStatusResponse {

    private static final String CONFIRMED = "예약";
    private static final String PENDING_STATUS_FORMAT = "%d번째 예약대기";

    private final Long reservationId;
    private final String themeName;
    private final LocalDate date;
    private final LocalTime time;
    private final String status;
    private final PaymentWebResponse payment;

    public static List<ReservationWithStatusResponse> fromReservationPayments(
            final List<ReservationPayment> reservationPayments
    ) {
        return reservationPayments.stream()
                .map(reservationPayment -> ReservationWithStatusResponse.from(
                        reservationPayment.getReservation(),
                        reservationPayment.getPayment()
                ))
                .toList();
    }

    public static List<ReservationWithStatusResponse> fromReservationWaits(
            final List<ReservationWaitWithRankResponse> reservationWaits
    ) {
        return reservationWaits.stream()
                .map(reservationWaitWithRank -> ReservationWithStatusResponse.of(
                        reservationWaitWithRank.reservationWait(),
                        reservationWaitWithRank.rank()))
                .toList();
    }

    public static ReservationWithStatusResponse from(
            final Reservation reservation,
            final Payment payment
    ) {
        return new ReservationWithStatusResponse(
                reservation.getId(),
                reservation.getTheme().getName().getValue(),
                reservation.getDate().getValue(),
                reservation.getTime().getStartAt(),
                CONFIRMED,
                PaymentWebResponse.from(payment)
        );
    }

    public static ReservationWithStatusResponse of(
            final ReservationWait reservationWait,
            final Long rank
    ) {
        return new ReservationWithStatusResponse(
                reservationWait.getId(),
                reservationWait.getTheme().getName().getValue(),
                reservationWait.getDate().getValue(),
                reservationWait.getTime().getStartAt(),
                String.format(PENDING_STATUS_FORMAT, rank),
                null
        );
    }
}
