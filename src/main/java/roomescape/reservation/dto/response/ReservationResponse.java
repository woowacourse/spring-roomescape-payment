package roomescape.reservation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.theme.dto.ThemeResponse;

public record ReservationResponse(
        Long id,
        LocalDate date,
        @JsonProperty("member") MemberResponse member,
        @JsonProperty("time") ReservationTimeResponse time,
        @JsonProperty("theme") ThemeResponse theme,
        String paymentKey,
        String amount
) {

    public static ReservationResponse from(final Reservation reservation) {
        Payment payment = reservation.getPayment();
        return new ReservationResponse(
                reservation.getId(),
                reservation.getDate(),
                MemberResponse.fromEntity(reservation.getMember()),
                ReservationTimeResponse.from(reservation.getReservationTime()),
                ThemeResponse.from(reservation.getTheme()),
                payment == null ? "" : payment.getPaymentKey(),
                payment == null ? "" : payment.getPaymentAmount()
        );
    }
}
