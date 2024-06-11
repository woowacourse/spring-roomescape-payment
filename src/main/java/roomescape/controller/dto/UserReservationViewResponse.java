package roomescape.controller.dto;

import roomescape.domain.reservation.Payment;
import roomescape.service.dto.response.UserReservationResponse;

import java.time.LocalDate;
import java.time.LocalTime;


public record UserReservationViewResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        String amount
) {
    private static final String BLANK_STRING = "";

    public static UserReservationViewResponse from(UserReservationResponse userReservationResponse) {
        return new UserReservationViewResponse(
                userReservationResponse.id(),
                userReservationResponse.reservationSlot().getTheme().getName(),
                userReservationResponse.reservationSlot().getDate(),
                userReservationResponse.reservationSlot().getTime().getStartAt(),
                ReservationStatusMessageMapper.mapTo(userReservationResponse.status(), userReservationResponse.rank()),
                userReservationResponse.payment().map(Payment::getPaymentKey).orElse(BLANK_STRING),
                userReservationResponse.payment().map(Payment::getAmount).orElse(BLANK_STRING)
        );
    }
}
