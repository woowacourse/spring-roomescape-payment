package roomescape.reservation.service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.service.dto.response.MemberResponse;
import roomescape.payment.infraStructure.dto.response.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.service.dto.response.ThemeResponse;

import java.time.LocalDate;

@Schema(name = "MyReservationsResponse(나의 예약 조회 응답 DTO)")
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
        PaymentResponse paymentResponse = getPaymentResponse(reservation);

        return new ReservationResponse(reservation.getId(),
                memberResponse,
                reservation.getDate(),
                reservationTimeResponse,
                themeResponse,
                paymentResponse
        );
    }

    private static PaymentResponse getPaymentResponse(final Reservation reservation) {
        PaymentResponse paymentResponse = null;
        if (reservation.getPayment() != null) {
            paymentResponse = PaymentResponse.fromEntity(reservation.getPayment());
        }
        return paymentResponse;
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
