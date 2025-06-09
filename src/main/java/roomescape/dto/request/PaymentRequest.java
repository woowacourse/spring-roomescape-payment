package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "결제 요청 객체")
public record PaymentRequest(
        @NotNull
        @Schema(description = "금액", example = "1000")
        int amount,

        @Schema(description = "페이먼트 키")
        @NotNull
        String paymentKey,

        @Schema(description = "주문 ID")
        @NotNull
        String orderId) {
}
