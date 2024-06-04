package roomescape.service.response;

import roomescape.domain.PaidReservation;

import java.time.LocalDate;

public record PaidReservationDto(
        Long id,
        String name,
        LocalDate date,
        ReservationTimeDto time,
        ThemeDto themeDto,
        PaymentDto paymentDto) {

    public PaidReservationDto(PaidReservation paidReservation) {
        this(
                paidReservation.getId(),
                paidReservation.getName(),
                paidReservation.getDate(),
                new ReservationTimeDto(paidReservation.getTime()),
                new ThemeDto(paidReservation.getTheme()),
                new PaymentDto(paidReservation)
        );
    }
}
