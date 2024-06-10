package roomescape.dto.response.reservation;

import java.math.BigDecimal;
import roomescape.domain.reservation.CanceledReservation;

public record CanceledReservationResponse(
        CanceledReservation canceledReservation,
        String paymentKey,
        BigDecimal totalAmount
) {
}
