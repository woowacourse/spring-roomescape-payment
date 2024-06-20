package roomescape.dto.reservation;

import java.time.LocalDate;

public record ReservationWaitingRequest(
        Long memberId,
        LocalDate date,
        Long timeId,
        Long themeId
) {
    public ReservationWaitingRequest(ReservationWithPaymentRequest request, Long memberId) {
        this(memberId, request.date(), request.timeId(), request.themeId());
    }
}
