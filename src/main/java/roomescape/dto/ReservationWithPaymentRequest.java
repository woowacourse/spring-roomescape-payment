package roomescape.dto;

import java.time.LocalDate;

public record ReservationWithPaymentRequest(LocalDate date,
                                            long timeId,
                                            long themeId,
                                            String paymentKey,
                                            String orderId,
                                            int amount) {
}
