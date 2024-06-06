package roomescape.payment.pg;

import java.math.BigDecimal;

public record TossPaymentsPayment(
        String paymentKey,
        String orderId,
        String status,
        BigDecimal totalAmount
) {

    public boolean verify(TossPaymentsConfirmRequest request) {
        boolean amountEqual = totalAmount.equals(request.getAmount());
        boolean orderIdEqual = orderId.equals(request.getOrderId());
        boolean paymentKeyEqual = paymentKey.equals(request.getPaymentKey());

        return amountEqual && orderIdEqual && paymentKeyEqual;
    }
}
