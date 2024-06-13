package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 취소 응답 DTO 입니다.")
public record PaymentCancelResponse(
        @Schema(description = "취소된 결제 정보입니다.")
        String orderName,
        @Schema(description = "취소된 결제 금액입니다.")
        long totalAmount) {
}
