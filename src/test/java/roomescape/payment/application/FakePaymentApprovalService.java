package roomescape.payment.application;


import java.math.BigDecimal;

public class FakePaymentApprovalService implements PaymentApprovalService {

    @Override
    public void approvePayment(String orderId, BigDecimal amount, String paymentKey) {
        return;
    }
}