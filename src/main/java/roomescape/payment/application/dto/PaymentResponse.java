package roomescape.payment.application.dto;

public interface PaymentResponse {

    String orderId();

    String paymentKey();

    Long totalAmount();
}
