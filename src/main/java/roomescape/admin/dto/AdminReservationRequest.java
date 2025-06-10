package roomescape.admin.dto;

import java.time.LocalDate;
import roomescape.reservation.dto.TossPaymentRequest;

public record AdminReservationRequest(
        LocalDate date,
        Long themeId,
        Long timeId,
        Long memberId,
        TossPaymentRequest tossPaymentRequest
) {
}
