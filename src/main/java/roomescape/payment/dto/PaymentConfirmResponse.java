package roomescape.payment.dto;

public interface PaymentConfirmResponse {

    String getOrderId();

    String getPaymentKey();

    int getTotalAmount();
}
