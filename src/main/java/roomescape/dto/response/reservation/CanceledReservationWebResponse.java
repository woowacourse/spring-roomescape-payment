package roomescape.dto.response.reservation;

import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.domain.reservation.CanceledReservation;
import roomescape.dto.response.member.MemberResponse;
import roomescape.dto.response.theme.ThemeResponse;

public record CanceledReservationWebResponse(
        long id,
        MemberResponse member,
        LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        String status,
        String paymentKey,
        BigDecimal totalAmount
) {
    public static CanceledReservationWebResponse of(
            CanceledReservation canceledReservation, String paymentKey, BigDecimal totalAmount
    ) {
        return new CanceledReservationWebResponse(
                canceledReservation.getId(),
                MemberResponse.from(canceledReservation.getMember()),
                canceledReservation.getDate(),
                ReservationTimeResponse.from(canceledReservation.getTime()),
                ThemeResponse.from(canceledReservation.getTheme()),
                canceledReservation.getStatus().getValue(),
                paymentKey,
                totalAmount
        );
    }
}
