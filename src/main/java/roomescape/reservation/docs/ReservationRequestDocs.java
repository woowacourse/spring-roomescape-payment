package roomescape.reservation.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.reservation.dto.request.ReservationRequest;
import java.time.LocalDate;

@Schema(description = "예약 요청")
public record ReservationRequestDocs(
        @Schema(description = "예약 날짜", example = "2024-03-20")
        LocalDate date,
        @Schema(description = "시간 ID", example = "1")
        Long timeId,
        @Schema(description = "테마 ID", example = "1")
        Long themeId,
        @Schema(description = "결제 키", example = "payment_key_1234")
        String paymentKey,
        @Schema(description = "주문 ID", example = "order_1234")
        String orderId,
        @Schema(description = "결제 금액", example = "10000")
        Long amount) {

    public ReservationRequest toReservationRequest() {
        return new ReservationRequest(date, timeId, themeId, paymentKey, orderId, amount);
    }
} 