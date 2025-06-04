package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.entity.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Status;
import roomescape.waiting.domain.Waiting;

public record ReservationMineResponse(
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String status,
        String paymentKey,
        int amount
) {

    public static ReservationMineResponse from(final Reservation reservation, final Payment payment) {
        return new ReservationMineResponse(
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                Status.RESERVED.displayName(),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }


    public static ReservationMineResponse from(final Waiting waiting, final Long order) {
        return new ReservationMineResponse(
                waiting.getTheme().getName(),
                waiting.getDate(),
                waiting.getTime().getStartAt(),
                order + Status.WAITING.displayName(),
                null,
                0
        );
    }
}
