package roomescape.dto.response.reservation;

import java.math.BigDecimal;
import roomescape.domain.reservation.Reservation;

public record MyReservationResponse(
        Reservation reservation,
        String paymentKey,
        BigDecimal totalAmount
) {
}
