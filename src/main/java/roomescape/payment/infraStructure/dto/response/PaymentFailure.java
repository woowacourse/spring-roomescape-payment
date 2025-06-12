package roomescape.payment.infraStructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PaymentFailure(결제 실패 응답 DTO)")
public record PaymentFailure(
        String code,
        String message
) {
}
