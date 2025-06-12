package roomescape.payment.infrastructure.dto;

public class TossPaymentResponse {

    private String paymentKey;
    private String orderId;
    private Integer totalAmount;
    private String type;
    private String approvedAt;

    private TossPaymentResponse() {
    }

    public TossPaymentResponse(final String paymentKey, final String orderId, final Integer totalAmount,
                               final String type, final String approvedAt) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.type = type;
        this.approvedAt = approvedAt;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public String getType() {
        return type;
    }

    public String getApprovedAt() {
        return approvedAt;
    }
}
