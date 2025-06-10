package roomescape.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(hidden = true)
public record PaymentSuccessResponse(
        String paymentKey,
        int totalAmount
) {
}
