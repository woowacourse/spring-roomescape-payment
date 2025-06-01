package roomescape.payment.application.dto;

import java.math.BigDecimal;

public interface PaymentRequest {

    String paymentKey();

    String orderId();

    BigDecimal amount();
}
