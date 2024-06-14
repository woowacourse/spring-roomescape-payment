package roomescape.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.dto.service.ReservationPaymentWithRankResponse;

public record MyReservationResponse(
        long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal amount
) {
    public static MyReservationResponse from(ReservationPaymentWithRankResponse response) {
        return new MyReservationResponse(
                response.getId(),
                response.getTheme().getName(),
                response.getDate(),
                response.getTime(),
                response.getStatusMessage(),
                response.getPaymentKey(),
                response.getAmount()
        );
    }
}
