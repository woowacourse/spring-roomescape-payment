package roomescape.admin.dto;

import java.time.LocalDate;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;

public record AdminReservationRequest(LocalDate date, Long timeId, Long themeId, Long memberId) {
    public ReservationWithPaymentRequest getReservationRequest() {
        return new ReservationWithPaymentRequest(date, timeId, themeId, null, null, null);
    }
}
