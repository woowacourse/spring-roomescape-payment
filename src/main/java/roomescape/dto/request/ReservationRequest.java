package roomescape.dto.request;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Schema(description = "예약 시 요청 객체")
public record ReservationRequest(
        @Schema(description = "예약 날짜 (yyyy-MM-dd 형식)", example = "2025-06-20", requiredMode = REQUIRED)
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(description = "예약 테마 ID", example = "5", requiredMode = REQUIRED)
        long themeId,

        @Schema(description = "예약 시간 ID", example = "4", requiredMode = REQUIRED)
        long timeId,

        @Schema(description = "토스페이먼츠 결제 키", example = "tgen_20250609123456789", requiredMode = NOT_REQUIRED)
        String paymentKey,

        @Schema(description = "주문 ID", example = "order_20250609_001", requiredMode = NOT_REQUIRED)
        String orderId,

        @Schema(description = "결제 금액 (원)", example = "25000", requiredMode = NOT_REQUIRED)
        int amount,

        @Schema(description = "결제 수단", example = "카드", allowableValues = {"카드", "가상계좌", "간편결제"}, requiredMode = NOT_REQUIRED)
        String paymentType
) {
}
