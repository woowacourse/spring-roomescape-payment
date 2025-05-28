package roomescape.dto.reservation;

import java.time.LocalDate;

public record ReservationPaymentRequest(LocalDate date, Long themeId, Long timeId, Long memberId, String orderId,
                                        String amount, String paymentKey, String paymentType) {
}
