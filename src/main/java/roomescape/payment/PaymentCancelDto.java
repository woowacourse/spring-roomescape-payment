package roomescape.payment;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "결제 취소 dto", description = "결제 취소시 필요한 데이터를 전달한다.")
public record PaymentCancelDto(
        String cancelReason,
        String canceledAt,
        String cancelAmount,
        String cancelStatus
) {
}
