package roomescape.reservation.dto;

import java.time.LocalDate;

public record CreateReservationWithPaymentRequest(LocalDate date,
                                                  Long themeId,
                                                  Long timeId,
                                                  String paymentKey,
                                                  String orderId,
                                                  Long amount) {
}
