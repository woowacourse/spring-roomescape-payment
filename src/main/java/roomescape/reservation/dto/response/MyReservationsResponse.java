package roomescape.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingWithRank;

public record MyReservationsResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        String status,
        String paymentKey,
        Long amount
) {

    private static final String EMPTY_PAYMENT_KEY = "";
    private static final Long EMPTY_AMOUNT = 0L;

    public static MyReservationsResponse of(
            final Reservation reservation,
            final Payment payment
    ) {
        return new MyReservationsResponse(
                reservation.idValue(),
                reservation.themeName(),
                reservation.getDate(),
                reservation.startTime(),
                reservation.statusDescription(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }

    public static MyReservationsResponse from(final WaitingWithRank waitingWithRank) {
        return new MyReservationsResponse(
                waitingWithRank.waitingIdValue(),
                waitingWithRank.themeName(),
                waitingWithRank.getDate(),
                waitingWithRank.startTime(),
                String.valueOf(waitingWithRank.getRank()),
                EMPTY_PAYMENT_KEY,
                EMPTY_AMOUNT
        );
    }
}
