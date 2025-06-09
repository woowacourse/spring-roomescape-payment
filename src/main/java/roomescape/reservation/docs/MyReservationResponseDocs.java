package roomescape.reservation.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.dto.response.MyReservationResponse;

@Schema(description = "내 예약 응답")
public record MyReservationResponseDocs(
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
        long amount) {

    public static MyReservationResponseDocs from(MyReservationResponse response) {
        return new MyReservationResponseDocs(
                response.id(),
                response.theme(),
                response.date(),
                response.time(),
                response.status(),
                response.paymentKey(),
                response.amount()
        );
    }
} 