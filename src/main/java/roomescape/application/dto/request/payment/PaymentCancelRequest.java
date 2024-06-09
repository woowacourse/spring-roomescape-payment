package roomescape.application.dto.request.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "결제 취소 정보")
public record PaymentCancelRequest(
        @Schema(description = "결제 취소 사유", example = "사용자 요청")
        String cancelReason
) {
}
