package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.dto.service.ReservationWithRankAndPayment;

public record MyReservationResponse(
        @Schema(description = "예약 ID")
        long id,

        @Schema(description = "테마 이름")
        String theme,

        @Schema(description = "예약 날짜")
        LocalDate date,

        @Schema(description = "예약 시간")
        LocalTime time,

        @Schema(description = "예약 상태")
        String status,

        @Schema(description = "결제 키")
        String paymentKey,

        @Schema(description = "결제 금액")
        Long amount
) {
    public static MyReservationResponse from(ReservationWithRankAndPayment reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getStatusMessage(),
                reservation.getPaymentKey(),
                reservation.getPaymentAmount()
        );
    }
}
