package roomescape.core.dto.payment;

public class PaymentConfirmResponse {
    private Long id;
    private Integer totalAmount;
    private String orderId;
    private String paymentKey;

    public PaymentConfirmResponse() {
    }

    public PaymentConfirmResponse(final Long id, final Integer totalAmount, final String orderId,
                                  final String paymentKey) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.orderId = orderId;
        this.paymentKey = paymentKey;
    }

    public Long getId() {
        return id;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
