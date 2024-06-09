package roomescape.service.mapper;

import java.util.List;
import java.util.Optional;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;
import roomescape.dto.LoginMemberReservationResponse;
import roomescape.dto.ReservationWaitingResponse;

public class LoginMemberReservationResponseMapper {
    public static LoginMemberReservationResponse toResponse(Reservation reservation) {
        return new LoginMemberReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate(),
                reservation.getTime(),
                "예약",
                null, null);
    }

    public static LoginMemberReservationResponse toResponse(Reservation reservation, Optional<Payment> payment) {
        return payment.map(value -> new LoginMemberReservationResponse(
                        reservation.getId(),
                        reservation.getThemeName(),
                        reservation.getDate(),
                        reservation.getTime(),
                        "예약",
                        value.getPaymentKey(),
                        value.getAmount()))
                .orElseGet(() -> toResponse(reservation));
    }

    public static LoginMemberReservationResponse toResponse(Reservation reservation, List<Payment> payments) {
        Optional<Payment> paymentByReservation = payments.stream()
                .filter(payment -> payment.getReservation().equals(reservation))
                .findFirst();
        return toResponse(reservation, paymentByReservation);
    }

    public static LoginMemberReservationResponse from(ReservationWaitingResponse waitingResponse) {
        return new LoginMemberReservationResponse(
                waitingResponse.id(),
                waitingResponse.themeName(),
                waitingResponse.date(),
                waitingResponse.startAt(),
                "%d번째 예약 대기".formatted(waitingResponse.priority()),
                null, null
        );
    }
}
