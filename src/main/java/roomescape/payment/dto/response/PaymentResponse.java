package roomescape.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record PaymentResponse(
        String paymentKey,
        String orderId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        LocalDateTime approvedAt,
        Long totalAmount
) {
}
