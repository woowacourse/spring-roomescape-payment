package roomescape.dto.reservation;

import java.time.format.DateTimeFormatter;

import roomescape.domain.reservation.Reservation;

public record MyReservationWithRankResponse(
        Long reservationId,
        String theme,
        String date,
        String time,
        String status,
        Long rank,
        String paymentKey,
        Long amount
) {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public MyReservationWithRankResponse(final Reservation reservation, final Long rank) {
        this(
                reservation.getId(),
                reservation.getTheme().getName(),
                DATE_FORMATTER.format(reservation.getDate()),
                TIME_FORMATTER.format(reservation.getStartAt()),
                reservation.getStatus().value(),
                rank,
                reservation.getPaymentKey().orElse(null),
                reservation.getPaymentAmount().orElse(null)
        );
    }
}
