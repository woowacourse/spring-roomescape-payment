package roomescape.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentApproveResponse(
        @NotNull(message = "paymentKey가 없습니다.") String paymentKey,
        @NotNull(message = "orderId가 없습니다.") String orderId
) {
}
