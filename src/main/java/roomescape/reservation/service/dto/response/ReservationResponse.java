package roomescape.reservation.service.dto.response;

import roomescape.member.service.dto.response.MemberResponse;
import roomescape.payment.infraStructure.dto.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.service.dto.response.ThemeResponse;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        PaymentResponse paymentResponse
) {

    public static ReservationResponse from(final Reservation reservation) {
        ReservationTimeResponse reservationTimeResponse = ReservationTimeResponse.from(
                reservation.getTime()
        );
        ThemeResponse themeResponse = ThemeResponse.from(reservation.getTheme());
        MemberResponse memberResponse = MemberResponse.fromEntity(reservation.getMember());
        PaymentResponse paymentResponse = PaymentResponse.fromEntity(reservation.getPayment());

        return new ReservationResponse(reservation.getId(),
                memberResponse,
                reservation.getDate(),
                reservationTimeResponse,
                themeResponse,
                paymentResponse
        );
    }

    public static ReservationResponse fromWithoutPayment(final Reservation reservation) {
        ReservationTimeResponse reservationTimeResponse = ReservationTimeResponse.from(
                reservation.getTime()
        );
        ThemeResponse themeResponse = ThemeResponse.from(reservation.getTheme());
        MemberResponse memberResponse = MemberResponse.fromEntity(reservation.getMember());

        return new ReservationResponse(reservation.getId(),
                memberResponse,
                reservation.getDate(),
                reservationTimeResponse,
                themeResponse,
                null
        );
    }
}
