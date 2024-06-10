package roomescape.service.reservation.dto;

import java.time.LocalDate;
import roomescape.config.DateFormatConstraint;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.service.schedule.dto.ReservationTimeResponse;
import roomescape.service.theme.dto.ThemeResponse;

public record ReservationResponse(
        long id,
        String name,
        @DateFormatConstraint LocalDate date,
        ReservationTimeResponse time,
        ThemeResponse theme,
        String paymentKey,
        long amount
) {
    public ReservationResponse(Reservation reservation) {
        this(reservation.getId(),
                reservation.getMember().getMemberName().getValue(),
                reservation.getDate(),
                new ReservationTimeResponse(reservation.getReservationTime()),
                new ThemeResponse(reservation.getTheme()),
                "결제 정보 없음",
                0
        );
    }

    public ReservationResponse(Reservation reservation, Payment payment) {
        this(reservation.getId(),
                reservation.getMember().getMemberName().getValue(),
                reservation.getDate(),
                new ReservationTimeResponse(reservation.getReservationTime()),
                new ThemeResponse(reservation.getTheme()),
                payment.getPaymentKey(),
                payment.getAmount()
        );
    }
}
