package roomescape.reservation.dto.response;

import java.time.LocalDate;
import roomescape.payment.model.Payment;
import roomescape.reservation.model.Reservation;

public record CreateReservationResponse(Long id,
                                        CreateMemberOfReservationResponse member,
                                        LocalDate date,
                                        CreateTimeOfReservationsResponse time,
                                        CreateThemeOfReservationResponse theme,
                                        String paymentKey,
                                        Long amountId) {
    public static CreateReservationResponse from(final Reservation reservation) {
        return new CreateReservationResponse(
                reservation.getId(),
                CreateMemberOfReservationResponse.from(reservation.getMember()),
                reservation.getDate(),
                CreateTimeOfReservationsResponse.from(reservation.getReservationTime()),
                CreateThemeOfReservationResponse.from(reservation.getTheme()),
                null,
                null
        );
    }

    public static CreateReservationResponse from(final Reservation reservation, final Payment payment) {
        return new CreateReservationResponse(
                reservation.getId(),
                CreateMemberOfReservationResponse.from(reservation.getMember()),
                reservation.getDate(),
                CreateTimeOfReservationsResponse.from(reservation.getReservationTime()),
                CreateThemeOfReservationResponse.from(reservation.getTheme()),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}
