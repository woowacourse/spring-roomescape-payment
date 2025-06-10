package roomescape.reservation.dto;

import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.dto.ThemeResponse;

public record CreateReservationResponse(
        Long id,
        MemberResponse member,
        ThemeResponse theme,
        ReservationTimeResponse time,
        LocalDate date,
        PaymentResponse paymentResponse
) {

    public static CreateReservationResponse of(final Reservation reservation, final Payment payment) {
        return new CreateReservationResponse(
                reservation.getId(),
                MemberResponse.from(reservation.getMember()),
                ThemeResponse.from(reservation.getTheme()),
                ReservationTimeResponse.from(reservation.getTime()),
                reservation.getDate(),
                PaymentResponse.from(payment)
        );
    }
}
