package roomescape.payment.dto.resonse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.time.LocalDateTime;

public record PaymentConfirmResponse(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String orderName,
        String status,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        LocalDateTime requestedAt,
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        LocalDateTime approvedAt
) {
}
