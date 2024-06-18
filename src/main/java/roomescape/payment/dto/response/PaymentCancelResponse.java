package roomescape.payment.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.OffsetDateTime;

@JsonDeserialize(using = PaymentCancelResponseDeserializer.class)
public record PaymentCancelResponse(
        String cancelStatus,
        String cancelReason,
        Long cancelAmount,
        OffsetDateTime canceledAt
) {
}
