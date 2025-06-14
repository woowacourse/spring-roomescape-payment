package roomescape.reservation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.domain.Reservation;

@Schema(description = "내 예약 응답")
public record MyReservationResponse(
        @Schema(description = "예약 ID", example = "1")
        Long id,
        @Schema(description = "테마명", example = "기본 테마")
        String theme,
        @Schema(description = "예약 날짜", example = "2024-03-20")
        String date,
        @Schema(description = "예약 시간", example = "10:00")
        String time,
        @Schema(description = "예약 상태", example = "예약")
        String status,
        @Schema(description = "결제 키", example = "payment_key_1234")
        String paymentKey,
        @Schema(description = "결제 금액", example = "10000")
        long amount
) {
    public static MyReservationResponse from(Reservation reservation) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getThemeName(),
                reservation.getDate().toString(),
                reservation.getReservationTime().toString(),
                "예약",
                reservation.getPayment().getPaymentKey(),
                reservation.getPayment().getAmount()
        );
    }
}
