package roomescape.controller.dto;

import roomescape.service.dto.response.UserReservationResponse;

import java.time.LocalDate;
import java.time.LocalTime;


public record UserReservationViewResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String paymentKey,
        String amount,
        String status
) {
    public static UserReservationViewResponse from(UserReservationResponse userReservationResponse) {
        return new UserReservationViewResponse(
                userReservationResponse.id(),
                userReservationResponse.reservationSlot().getTheme().getName(),
                userReservationResponse.reservationSlot().getDate(),
                userReservationResponse.reservationSlot().getTime().getStartAt(),
                PaymentStatusMessageMapper.mapToPaymentKey(userReservationResponse.payment()),
                PaymentStatusMessageMapper.mapToAmount(userReservationResponse.payment()), //TODO 리팩토링 필요
                ReservationStatusMessageMapper.mapTo(userReservationResponse.rank())
        );
    }
}
