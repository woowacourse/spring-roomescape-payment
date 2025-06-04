package roomescape.reservation.dto;

import java.time.LocalDate;

public record UserReservationCreateRequest(LocalDate date,
                                           Long themeId,
                                           Long timeId,
                                           String paymentKey,
                                           String orderId,
                                           Long amount) {
}
