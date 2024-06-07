package roomescape.payment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDateTime;

@JsonDeserialize(using = PaymentCancelResponseDeserializer.class)
public record PaymentCancelResponse(
        String cancelStatus,
        String cancelReason,
        Long cancelAmount,
        LocalDateTime canceledAt
) {
}
