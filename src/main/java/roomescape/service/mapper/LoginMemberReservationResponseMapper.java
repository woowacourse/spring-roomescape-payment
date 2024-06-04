package roomescape.service.mapper;

import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;
import roomescape.dto.LoginMemberReservationResponse;

public class LoginMemberReservationResponseMapper {
    public static LoginMemberReservationResponse toResponse(Reservation reservation, Payment payment) {
        if (payment == null) {
            return makeNonPaidResponse(reservation);
        }
        return makePaidResponse(reservation, payment);
    }

    private static LoginMemberReservationResponse makeNonPaidResponse(Reservation reservation) {
        return new LoginMemberReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getTime(),
                "결제 대기",
                null,
                null);
    }

    private static LoginMemberReservationResponse makePaidResponse(Reservation reservation, Payment payment) {
        return new LoginMemberReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getTime(),
                "예약",
                payment.getPaymentKey(),
                payment.getAmount());
    }
}
