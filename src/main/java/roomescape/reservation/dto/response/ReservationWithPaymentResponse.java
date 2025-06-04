package roomescape.reservation.dto.response;

import java.time.LocalDate;
import roomescape.member.dto.response.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationTime.dto.response.ReservationTimeResponse;
import roomescape.theme.dto.response.ThemeResponse;

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
                MemberResponse.from(reservation.getMember()),
                reservation.getDate(),
                ReservationTimeResponse.from(reservation.getTime()),
                ThemeResponse.from(reservation.getTheme()),
                payment.getId()
        );
    }
}
