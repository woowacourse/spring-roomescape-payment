package roomescape.reservation.service.dto.response;

import roomescape.member.service.dto.response.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.service.dto.response.ThemeResponse;

import java.time.LocalDate;

public record ReservationWithPaymentResponse(
        Long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        Long paymentId
) {
    public static ReservationWithPaymentResponse from(Reservation reservation, Payment payment) {
        return new ReservationWithPaymentResponse(
                reservation.getId(),
                MemberResponse.fromEntity(reservation.getMember()),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()),
                payment.getId()
        );
    }
}
