package roomescape.domain.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 확인 응답 DTO")
public interface PaymentConfirmResponse {

    String getOrderId();

    String getPaymentKey();

    int getTotalAmount();
}
