package roomescape.dto.response.reservation;

import java.math.BigDecimal;
import roomescape.domain.reservation.Reservation;

public record CanceledReservationsDto(
        Reservation reservation,
        String paymentKey,
        BigDecimal totalAmount
) {
}
