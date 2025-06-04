package roomescape.payment.dto;

public record TossPaymentConfirmResponse(String orderId, String paymentKey, int totalAmount)
        implements PaymentConfirmResponse {

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getPaymentKey() {
        return paymentKey;
    }

    @Override
    public int getTotalAmount() {
        return totalAmount;
    }
}