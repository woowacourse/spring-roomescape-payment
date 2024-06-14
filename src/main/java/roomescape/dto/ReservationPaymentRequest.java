package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "예약 결제 요청 DTO 입니다.")
public record ReservationPaymentRequest(
        @Schema(description = "예약 날짜입니다.")
        LocalDate date,
        @Schema(description = "테마 ID 입니다.")
        long themeId,
        @Schema(description = "예약 시간 ID 입니다.")
        long timeId,
        @Schema(description = "결제 키 값 입니다.")
        String paymentKey,
        @Schema(description = "주문 번호입니다.")
        String orderId,
        @Schema(description = "요청된 가격입니다.")
        long amount) {
}
