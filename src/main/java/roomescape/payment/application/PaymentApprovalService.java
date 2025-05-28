package roomescape.payment.application;

import java.math.BigDecimal;

public interface PaymentApprovalService {
    void approvePayment(String orderId, BigDecimal amount, String paymentKey);
}
