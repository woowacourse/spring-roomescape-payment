package roomescape.service.response;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationDate;

public record ReservationDto(
        Long id,
        String name,
        ReservationDate date,
        ReservationTimeDto time,
        ThemeDto theme,
        PaymentDto paymentDto) {

    public ReservationDto(Reservation reservation) {
        this(
                reservation.getId(),
                reservation.getMember().getName().getName(),
                reservation.getDate(),
                new ReservationTimeDto(reservation.getTime()),
                new ThemeDto(reservation.getTheme()),
                PaymentDto.from(reservation.getPayment())
        );
    }
}
